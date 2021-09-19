package ru.pixelmongo.pixelmongo.handlers.impl;

import java.util.Random;

import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenRandomValue;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;

@Component
public class DonatePackRandomValueTokenHandler implements DonatePackTokenHandler<DonatePackTokenRandomValue>{

    private Random random = new Random();

    @Override
    public DonatePackTokenType getTokenType() {
        return DonatePackTokenType.RANDOM_VALUE;
    }

    @Override
    public HandleResult processToken(DonatePackTokenRandomValue token, Object... data) {
        String result = "";
        int size = token.getValues().size();
        if(size > 0)
            result = token.getValues().get(random.nextInt(size));
        return new HandleResult(result, 0);
    }

}
