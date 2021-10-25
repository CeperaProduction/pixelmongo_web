package ru.pixelmongo.pixelmongo.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ru.pixelmongo.pixelmongo.exceptions.DonateExtraNotFoundException;
import ru.pixelmongo.pixelmongo.exceptions.DonateNotEnoughMoneyException;
import ru.pixelmongo.pixelmongo.exceptions.DonatePackActiveException;
import ru.pixelmongo.pixelmongo.exceptions.DonatePackTokenFormatException;
import ru.pixelmongo.pixelmongo.exceptions.DonatePackTokenProcessExcetion;
import ru.pixelmongo.pixelmongo.handlers.DonateExtraHandler;
import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenHandler;
import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenProcessResult;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateExtraRecord;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateQuery;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateServer;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackToken;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePackTokenData;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateExtraRecordRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateQueryRepository;
import ru.pixelmongo.pixelmongo.services.DonateService;

@Service("donateService")
public class DonateServiceImpl implements DonateService{

    @Autowired
    private DonateQueryRepository queries;

    @Autowired
    private UserRepository users;

    @Autowired
    @Qualifier("defaultLocale")
    private Locale defaultLocale;

    @Autowired
    private MessageSource msg;

    @Autowired
    private DonateExtraRecordRepository extraRecords;

    private Map<DonatePackTokenType, DonatePackTokenHandler<?>> tokenHandlers = new HashMap<>();

    private Map<String, DonateExtraHandler> extraHandlers = new HashMap<>();

    private String packBackPrefix = "";

    @Autowired
    public void setupTokenHandlers(List<DonatePackTokenHandler<?>> handlers) {
        handlers.forEach(h->tokenHandlers.put(h.getTokenType(), h));
        LOGGER.info("Found "+tokenHandlers.size()+" donate pack token handlers");
    }

    @Autowired
    public void setupExtraHandlers(List<DonateExtraHandler> handlers) {
        handlers.forEach(h->extraHandlers.put(h.getExtraTag(), h));
        LOGGER.info("Found "+extraHandlers.size()+" donate extras handlers");
    }


    @Autowired
    private void setupMessages(MessageSource msg, @Qualifier("defaultLocale") Locale defaultLocale) {
        packBackPrefix = msg.getMessage("donate.pack.back.prefix", null, "", defaultLocale);
        if(!packBackPrefix.isEmpty()) packBackPrefix += ' ';
    }

    @Override
    public DonatePackToken makeToken(DonatePackTokenData tokenData, DonatePack pack) {
        try {
            return getTokenHandler(tokenData.getType()).makeToken(tokenData, pack);
        }catch(Exception ex) {
            throw new DonatePackTokenFormatException(tokenData.getName(), ex);
        }
    }

    @Override
    public DonatePackTokenData makeTokenData(DonatePackToken token) {
        return getTokenHandler(token.getType()).makeData(token);
    }

    @SuppressWarnings("unchecked")
    private <T extends DonatePackToken> DonatePackTokenHandler<T> getTokenHandler(DonatePackTokenType tokenType){
        DonatePackTokenHandler<?> handler = tokenHandlers.get(tokenType);
        if(handler == null)
            throw new RuntimeException("Token handler for token type "+tokenType+" not found!");
        return (DonatePackTokenHandler<T>) handler;
    }

    @Override
    public DonatePackTokenProcessResult processToken(DonatePackToken token, List<String> data) {
        try {
            return getTokenHandler(token.getType()).processToken(token, data);
        }catch(Exception ex) {
            throw new DonatePackTokenProcessExcetion(token.getToken(), data, ex);
        }
    }

    @Override
    public DonateQuery processPack(DonatePack pack,
            String serverName,
            String playerName,
            Map<String, List<String>> tokensData,
            boolean makeBackIfTimed) {

        if(tokensData == null) tokensData = new HashMap<>();

        Set<Pair<String, String>> tokenValues = new TreeSet<>((a,b)->b.getFirst().compareTo(a.getFirst()));

        //Don't need to process $player token here. It will be processed by MegaQuery
        //tokenValues.add(Pair.of("player", playerName));


        float costMult = 1f - Math.min(Math.max(pack.getDiscount() / 100f, 0), 1f);
        int tokensCostChange = 0;
        for(DonatePackToken token : pack.getTokens()) {
            DonatePackTokenProcessResult result
                = processToken(token, tokensData.getOrDefault(token.getToken(), Collections.emptyList()));
            tokenValues.add(Pair.of(token.getToken(), result.getTokenValue()));
            tokensCostChange += (int)Math.round(result.getCostChange()*costMult);
        }

        DonateQuery query = new DonateQuery(pack.getTitle(), serverName, playerName);
        List<String> cmds = pack.getCommands().stream().map(cmd->applyTokens(cmd, tokenValues))
                .collect(Collectors.toList());
        query.setCommands(cmds);
        query.setHidden(pack.isHiddenGive());
        query.setInvSpace(pack.getInvSpace());
        query.setOffline(pack.isGiveOffline());
        query.setPackId(pack.getId());

        int cost = pack.getActualCost() + tokensCostChange;

        query.setSpentMoney(cost > 0 ? cost : 0);

        if(makeBackIfTimed && pack.isTimed()) {

            DonateQuery backQuery = query.duplicate();
            List<String> backCmds = pack.getBackCommands().stream().map(cmd->applyTokens(cmd, tokenValues))
                    .collect(Collectors.toList());
            backQuery.setCommands(backCmds);
            backQuery.setDate(query.getDate());
            backQuery.setExecuteAfter(query.getExecuteAfter()+pack.getExistTime());
            backQuery.setTitle(packBackPrefix+query.getTitle());
            backQuery.setInvSpace(0);
            backQuery.setSpentMoney(0);

            query.setBack(backQuery);
            backQuery.setBackOf(query);
        }

        return query;
    }

    private static String applyTokens(String cmd, Iterable<Pair<String, String>> tokenValues) {
        for(Pair<String, String> p : tokenValues)
            cmd = cmd.replace('$'+p.getFirst(), p.getSecond());
        return cmd;
    }

    @Override
    public int buyPack(DonatePack pack, User user, DonateServer server,
            Map<String, List<String>> tokensData, int count, boolean fromAdmin) {

        if(count < 1) count = 1;
        if(count > 1 && pack.isTimed()) {
            LOGGER.warn("Illegal pack count "+count+" for timed pack "+pack.getTitle()+". Timed packs can't be countable.");
            count = 1;
        }

        DonateQuery activeBackQuery = this.queries.getActiveBackQuery(server, user, pack).orElse(null);
        if(activeBackQuery != null && !fromAdmin)
            throw new DonatePackActiveException(pack.getId(), activeBackQuery.getId(),
                    new Date(activeBackQuery.getExecuteAfter()*1000L));

        List<DonateQuery> queries = new ArrayList<>();
        int sum = 0;
        int given = 0;
        if(count > 1) {
            for(int i = 0; i < count; i++) {
                DonateQuery query = processPack(pack, server.getConfigName(), user.getName(), tokensData, false);
                queries.add(query);
                ++given;
                if(fromAdmin) {
                    query.setSpentMoney(0);
                }else {
                    sum += query.getSpentMoney();
                }
            }
        }else {
            DonateQuery query = processPack(pack, server.getConfigName(), user.getName(), tokensData, true);
            queries.add(query);
            given = 1;
            if(fromAdmin) {
                query.setSpentMoney(0);
            }else {
                sum = query.getSpentMoney();
            }
            if(query.getBack() != null)
                queries.add(query.getBack());
        }

        consumeMoney(user, sum);

        if(activeBackQuery != null)
            this.queries.delete(activeBackQuery);

        this.queries.saveAll(queries);

        return given;
    }

    @Override
    public int consumeMoney(User user, int sum) {
        int balance = user.getBalance();
        if(sum <= 0) return balance;

        int newBalance = balance - sum;

        if(newBalance < 0)
            throw new DonateNotEnoughMoneyException(user.getName(), sum, balance);

        user.setBalance(newBalance);

        users.save(user);

        return newBalance;
    }

    @Override
    public void logExtra(User user, int spentMoney, String content) {
        DonateExtraRecord record = new DonateExtraRecord(user, spentMoney, content);
        extraRecords.save(record);
    }

    @Override
    public void logExtra(User user, int spentMoney, String contentLangKey, Object[] contentLangValues) {
        logExtra(user, spentMoney, msg.getMessage(contentLangKey, contentLangValues, defaultLocale));
    }

    @Override
    public Page<DonateExtraRecord> getExtraLogs(String search, Pageable limits){
        if(!StringUtils.hasText(search)) {
            return extraRecords.findAllByOrderByDateDesc(limits);
        }
        return extraRecords.search(search, limits);
    }

    @Override
    public List<String> getExtraTags(){
        return new ArrayList<String>(extraHandlers.keySet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DonateExtraHandler> T getExtraHandler(String tag) {
        DonateExtraHandler extraHandler = extraHandlers.get(tag);
        if(extraHandler == null)
            throw new DonateExtraNotFoundException(tag);
        return (T) extraHandler;
    }

    @Override
    public ResultMessage buyExtra(String extra, User user, Locale loc, boolean forFree) {
        return getExtraHandler(extra).buy(this, user, loc, forFree);
    }


}
