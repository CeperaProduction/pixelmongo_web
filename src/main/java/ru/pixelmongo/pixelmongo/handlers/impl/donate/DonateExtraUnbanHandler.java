package ru.pixelmongo.pixelmongo.handlers.impl.donate;

import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.DonateExtraHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.sub.PlayerBanRecord;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.sub.PlayerBanRecordRepository;
import ru.pixelmongo.pixelmongo.services.DonateService;

@Component
public class DonateExtraUnbanHandler implements DonateExtraHandler{


    @Autowired
    private MessageSource msg;

    @Autowired
    private PlayerBanRecordRepository banlist;

    private int dayCost, maxCost;

    @Autowired
    public void setupParams(Environment env) {
        dayCost = env.getRequiredProperty("donate.extra.unban.cost.day", Integer.class);
        maxCost = env.getRequiredProperty("donate.extra.unban.cost.max", Integer.class);
    }

    @Override
    public String getExtraTag() {
        return "unban";
    }

    @Override
    public int getCost(User user) {
        return banlist.getActiveBan(user).map(this::getCost).orElse(0);
    }

    public int getDayCost() {
        return dayCost;
    }

    public int getMaxCost() {
        return maxCost;
    }

    @Override
    public ResultMessage buy(DonateService donate, User user, Locale loc, boolean forFree) {
        PlayerBanRecord ban = banlist.getActiveBan(user).orElse(null);
        if(ban == null)
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("donate.extra.unban.already", null, loc));
        if(forFree) {
            banlist.delete(user);
        }else {
            int cost = getCost(ban);
            int[] consumed = donate.consumeMoney(user, cost);
            banlist.delete(user);
            donate.logExtra(user, consumed[0], consumed[1], "donate.extra.unban", null);
        }
        return new ResultMessage(DefaultResult.OK, msg.getMessage("donate.extra.unban.bought", null, loc));
    }

    private int getCost(PlayerBanRecord ban) {
        Date end = ban.getEndDate();
        if(end == null) return maxCost;
        int s = (int) (Math.max(end.getTime() - System.currentTimeMillis(), 0) / 1000);
        return Math.min(((int) Math.ceil(1.0D * s / 86400)) * dayCost, maxCost);
    }

}
