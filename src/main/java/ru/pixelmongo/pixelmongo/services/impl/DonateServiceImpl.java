package ru.pixelmongo.pixelmongo.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.pixelmongo.pixelmongo.exceptions.DonatePackTokenFormatException;
import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackToken;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePackTokenData;
import ru.pixelmongo.pixelmongo.services.DonateService;

@Service("donateService")
public class DonateServiceImpl implements DonateService{

    private Map<DonatePackTokenType, DonatePackTokenHandler<?>> tokenHandlers = new HashMap<>();

    @Autowired
    public void setupHandlers(List<DonatePackTokenHandler<?>> handlers) {
        handlers.forEach(h->tokenHandlers.put(h.getTokenType(), h));
        LOGGER.info("Found "+tokenHandlers.size()+" donate pack token handlers");
    }

    @Override
    public DonatePackToken makeToken(DonatePackTokenData tokenData, DonatePack pack) {
        try {
            return getHandler(tokenData.getType()).makeToken(tokenData, pack);
        }catch(Exception ex) {
            throw new DonatePackTokenFormatException(tokenData.getName(), ex);
        }
    }

    @Override
    public DonatePackTokenData makeTokenData(DonatePackToken token) {
        return getHandler(token.getType()).makeData(token);
    }

    @SuppressWarnings("unchecked")
    private <T extends DonatePackToken> DonatePackTokenHandler<T> getHandler(DonatePackTokenType tokenType){
        DonatePackTokenHandler<?> handler = tokenHandlers.get(tokenType);
        if(handler == null)
            throw new RuntimeException("Token handler for token type "+tokenType+" not found!");
        return (DonatePackTokenHandler<T>) handler;
    }

}
