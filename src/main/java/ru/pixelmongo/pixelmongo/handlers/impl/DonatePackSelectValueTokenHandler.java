package ru.pixelmongo.pixelmongo.handlers.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenHandler;
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
    public ProcessResult processToken(DonatePackTokenSelectValue token, Object... data) {
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
        return new ProcessResult(value, costChange);
    }

    @Override
    public DonatePackTokenSelectValue makeToken(DonatePackTokenData data, DonatePack pack) throws Exception{
        if(data.getType() != getTokenType())
            throw new IllegalArgumentException("Wrong token type");
        List<String> values = new ArrayList<String>();
        List<Integer> costValues = new ArrayList<Integer>();
        List<String> valuesDisplay = new ArrayList<String>();
        for(int i = 0; i < data.getOptions().size(); i+=3) {
            values.add(data.getOption(i));
            costValues.add(Integer.parseInt(data.getOption(i+1)));
            valuesDisplay.add(data.getOption(i+2));
        }
        return new DonatePackTokenSelectValue(data.getName(), pack, values, costValues, valuesDisplay);
    }

    @Override
    public DonatePackTokenData makeData(DonatePackTokenSelectValue token) {
        List<String> options = new ArrayList<String>();
        for(int i = 0; i < token.getValues().size(); i++) {
            options.add(token.getValues().get(i));
            options.add(token.getCostValues().get(i).toString());
            options.add(token.getValuesDisplay().get(i));
        }
        return new DonatePackTokenData(token.getType(), token.getToken(), options);
    }

}
