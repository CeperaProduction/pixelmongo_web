package ru.pixelmongo.pixelmongo.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackToken;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePackTokenData;

public interface DonateService {

    public static final Logger LOGGER = LogManager.getLogger(DonateService.class);

    public DonatePackToken makeToken(DonatePackTokenData tokenData, DonatePack pack);

    public DonatePackTokenData makeTokenData(DonatePackToken token);

}
