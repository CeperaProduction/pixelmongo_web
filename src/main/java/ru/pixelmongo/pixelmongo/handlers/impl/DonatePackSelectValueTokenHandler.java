package ru.pixelmongo.pixelmongo.handlers.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenHandler;
import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenProcessResult;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenSelectValue;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePackTokenData;

@Component
public class DonatePackSelectValueTokenHandler implements DonatePackTokenHandler<DonatePackTokenSelectValue>{

    @Override
    public DonatePackTokenType getTokenType() {
        return DonatePackTokenType.SELECT_VALUE;
    }

    @Override
    public DonatePackTokenProcessResult processToken(DonatePackTokenSelectValue token, List<String> data) throws Exception {
        if(data == null || data.size() == 0)
            throw new IllegalArgumentException("Data length is 0. Must be at least 1");
        int index = Integer.parseInt(data.get(0));
        String value = token.getValues().get(index);
        int costChange = token.getCostValues().get(index);
        return new DonatePackTokenProcessResultImpl(value, costChange);
    }

    @Override
    public DonatePackTokenSelectValue makeToken(DonatePackTokenData data, DonatePack pack) throws Exception{
        if(data.getType() != getTokenType())
            throw new IllegalArgumentException("Wrong token type");
        String display = data.getOption(0);
        List<String> values = new ArrayList<String>();
        List<Integer> costValues = new ArrayList<Integer>();
        List<String> valuesDisplay = new ArrayList<String>();
        for(int i = 1; i < data.getOptions().size(); i+=3) {
            values.add(data.getOption(i));
            costValues.add(Integer.parseInt(data.getOption(i+1)));
            valuesDisplay.add(data.getOption(i+2));
        }
        return new DonatePackTokenSelectValue(data.getName(), pack, display, values, costValues, valuesDisplay);
    }

    @Override
    public DonatePackTokenData makeData(DonatePackTokenSelectValue token) {
        List<String> options = new ArrayList<String>();
        options.add(token.getDisplay());
        for(int i = 0; i < token.getValues().size(); i++) {
            options.add(token.getValues().get(i));
            options.add(token.getCostValues().get(i).toString());
            options.add(token.getValuesDisplay().get(i));
        }
        return new DonatePackTokenData(token.getType(), token.getToken(), options);
    }

}
