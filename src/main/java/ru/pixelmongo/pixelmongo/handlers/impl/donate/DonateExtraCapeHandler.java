package ru.pixelmongo.pixelmongo.handlers.impl.donate;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.DonateExtraHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.services.DonateService;

@Component
public class DonateExtraCapeHandler implements DonateExtraHandler{

    @Autowired
    private UserRepository users;

    @Autowired
    private MessageSource msg;

    private int cost;

    @Autowired
    public void setupParams(Environment env) {
        cost = env.getRequiredProperty("donate.extra.cape.cost", Integer.class);
    }

    @Override
    public String getExtraTag() {
        return "cape";
    }

    @Override
    public int getCost(User user) {
        return cost;
    }

    @Override
    public ResultMessage buy(DonateService donate, User user, Locale loc, boolean forFree) {
        if(user.hasCape())
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("donate.extra.cape.already", null, loc));
        if(!forFree) {
            int cost = getCost(user);
            int[] consumed = donate.consumeMoney(user, cost);
            user.setHasCape(true);
            users.save(user);
            donate.logExtra(user, consumed[0], consumed[1], "donate.extra.cape", null);
        }else {
            user.setHasCape(true);
            users.save(user);
        }
        return new ResultMessage(DefaultResult.OK, msg.getMessage("donate.extra.cape.bought", null, loc));
    }

}
