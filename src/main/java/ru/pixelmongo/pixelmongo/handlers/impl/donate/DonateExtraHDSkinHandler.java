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
public class DonateExtraHDSkinHandler implements DonateExtraHandler{

    @Autowired
    private UserRepository users;

    @Autowired
    private MessageSource msg;

    private int cost;

    @Autowired
    public void setupParams(Environment env) {
        cost = env.getRequiredProperty("donate.extra.hd_skin.cost", Integer.class);
    }

    @Override
    public String getExtraTag() {
        return "hd_skin";
    }

    @Override
    public int getCost(User user) {
        return cost;
    }

    @Override
    public ResultMessage buy(DonateService donate, User user, Locale loc, boolean forFree) {
        if(user.hasHDSkin())
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("donate.extra.hd_skin.already", null, loc));
        if(!forFree) {
            int cost = getCost(user);
            donate.consumeMoney(user, cost);
            user.setHasHDSkin(true);
            users.save(user);
            donate.logExtra(user, cost, "donate.extra.hd_skin", null);
        }else {
            user.setHasHDSkin(true);
            users.save(user);
        }
        return new ResultMessage(DefaultResult.OK, msg.getMessage("donate.extra.hd_skin.bought", null, loc));
    }

}
