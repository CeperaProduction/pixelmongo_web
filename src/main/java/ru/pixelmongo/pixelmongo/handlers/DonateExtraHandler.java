package ru.pixelmongo.pixelmongo.handlers;

import java.util.Locale;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.services.DonateService;

public interface DonateExtraHandler {

    public String getExtraTag();

    public int getCost(User user);

    public ResultMessage buy(DonateService donate, User user, Locale loc, boolean forFree);

}
