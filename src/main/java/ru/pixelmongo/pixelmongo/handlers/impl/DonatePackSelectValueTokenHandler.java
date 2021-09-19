package ru.pixelmongo.pixelmongo.handlers.impl;

import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenSelectValue;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;

@Component
public class DonatePackSelectValueTokenHandler implements DonatePackTokenHandler<DonatePackTokenSelectValue>{

    @Override
    public DonatePackTokenType getTokenType() {
        return DonatePackTokenType.SELECT_VALUE;
    }

    @Override
    public HandleResult processToken(DonatePackTokenSelectValue token, Object... data) {
        if(data.length == 0)
            throw new IllegalArgumentException("Data length is 0. Must be at least 1");
        int index;
        if(data[0] instanceof Integer)
            index = (int) data[0];
        else
            try {
                index = Integer.parseInt(data[0].toString());
            }catch(NumberFormatException ex) {
                throw new IllegalArgumentException(ex);
            }
        String value = token.getValues().get(index);
        int costChange = token.getCostValues().get(index);
        return new HandleResult(value, costChange);
    }

}
