package ru.pixelmongo.pixelmongo.handlers.impl;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenRandomValue;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePackTokenData;

@Component
public class DonatePackRandomValueTokenHandler implements DonatePackTokenHandler<DonatePackTokenRandomValue>{

    private Random random = new Random();

    @Override
    public DonatePackTokenType getTokenType() {
        return DonatePackTokenType.RANDOM_VALUE;
    }

    @Override
    public ProcessResult processToken(DonatePackTokenRandomValue token, Object... data) {
        String result = "";
        int size = token.getValues().size();
        if(size > 0)
            result = token.getValues().get(random.nextInt(size));
        return new ProcessResult(result, 0);
    }

    @Override
    public DonatePackTokenRandomValue makeToken(DonatePackTokenData data, DonatePack pack) {
        if(data.getType() != getTokenType())
            throw new IllegalArgumentException("Wrong token type");
        List<String> values = data.getOptions();
        return new DonatePackTokenRandomValue(data.getName(), pack, values);
    }

    @Override
    public DonatePackTokenData makeData(DonatePackTokenRandomValue token) {
        return new DonatePackTokenData(token.getType(), token.getToken(), token.getValues());
    }

}
