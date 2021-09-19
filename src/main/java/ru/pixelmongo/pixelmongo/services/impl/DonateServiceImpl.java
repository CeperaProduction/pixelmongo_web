package ru.pixelmongo.pixelmongo.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;
import ru.pixelmongo.pixelmongo.services.DonateService;

@Service("donateService")
public class DonateServiceImpl implements DonateService{

    private Map<DonatePackTokenType, DonatePackTokenHandler<?>> tokenHandlers = new HashMap<>();

    @Autowired
    public void setupHandlers(List<DonatePackTokenHandler<?>> handlers) {
        handlers.forEach(h->tokenHandlers.put(h.getTokenType(), h));
        LOGGER.info("Found "+tokenHandlers.size()+" donate pack token handlers");
    }

}
