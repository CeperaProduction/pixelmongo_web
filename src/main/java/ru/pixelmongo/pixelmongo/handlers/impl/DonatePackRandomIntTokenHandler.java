package ru.pixelmongo.pixelmongo.handlers.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenRandomInt;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePackTokenData;

@Component
public class DonatePackRandomIntTokenHandler implements DonatePackTokenHandler<DonatePackTokenRandomInt>{

    private Random random = new Random();

    @Override
    public DonatePackTokenType getTokenType() {
        return DonatePackTokenType.RANDOM_INT;
    }

    @Override
    public ProcessResult processToken(DonatePackTokenRandomInt token, Object... data) {
        int result = 0;
        if(token.getMax() >= token.getMin())
            result = token.getMin() + random.nextInt(token.getMax() - token.getMin() + 1);
        return new ProcessResult(result+"", 0);
    }

    @Override
    public DonatePackTokenRandomInt makeToken(DonatePackTokenData data, DonatePack pack) throws Exception {
        if(data.getType() != getTokenType())
            throw new IllegalArgumentException("Wrong token type");
        int min = Integer.parseInt(data.getOptions().get(0));
        int max = Integer.parseInt(data.getOptions().get(1));
        return new DonatePackTokenRandomInt(data.getName(), pack, min, max);
    }

    @Override
    public DonatePackTokenData makeData(DonatePackTokenRandomInt token) {
        List<String> settings = new ArrayList<>();
        settings.add(token.getMin()+"");
        settings.add(token.getMax()+"");
        return new DonatePackTokenData(token.getType(), token.getToken(), settings);
    }

}
