package ru.pixelmongo.pixelmongo.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.pixelmongo.pixelmongo.exceptions.DonateNotEnoughMoneyException;
import ru.pixelmongo.pixelmongo.exceptions.DonatePackActiveException;
import ru.pixelmongo.pixelmongo.handlers.DonateExtraHandler;
import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenProcessResult;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateExtraRecord;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateQuery;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateServer;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackToken;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePackTokenData;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;

public interface DonateService {

    public static final Logger LOGGER = LogManager.getLogger(DonateService.class);

    public DonatePackToken makeToken(DonatePackTokenData tokenData, DonatePack pack);

    public DonatePackTokenData makeTokenData(DonatePackToken token);

    public DonatePackTokenProcessResult processToken(DonatePackToken token, List<String> data);

    /**
     * Prepares fully configured {@link DonateQuery} by given parameters.
     * If there is generated back query, its stored in {@link DonateQuery#getBack()}
     * of returned object.
     *
     * @param pack
     * @param serverName
     * @param playerName
     * @param tokensData - may be null
     * @param makeBackIfTimed
     * @return
     */
    public DonateQuery processPack(DonatePack pack, String serverName, String playerName, Map<String, List<String>> tokensData,
            boolean makeBackIfTimed);

    /**
     * Perform pack processing, user balance changes and create queries
     *
     * @param pack
     * @param user
     * @param server
     * @param tokensData
     * @param count - sets to 1 if not valid
     * @param fromAdmin - don't change user balance and recreate back query if exists
     *
     * @throws {@link DonateNotEnoughMoneyException}, {@link DonatePackActiveException}
     *
     * @return count of given packs
     */
    public int buyPack(DonatePack pack, User user, DonateServer server, Map<String, List<String>> tokensData, int count,
            boolean fromAdmin);

    /**
     * Take money from user
     * @param user
     * @param sum
     * @return new user balance
     * @throws DonateNotEnoughMoneyException if user has not enough money
     */
    public int consumeMoney(User user, int sum);

    public List<String> getExtraTags();

    public <T extends DonateExtraHandler> T getExtraHandler(String extraTag);

    public ResultMessage buyExtra(String extra, User user, Locale loc, boolean forFree);

    public void logExtra(User user, int spentMoney, String content);

    public void logExtra(User user, int spentMoney, String contentLangKey, Object[] contentLangValues);

    public Page<DonateExtraRecord> getExtraLogs(String search, Pageable limits);

}
