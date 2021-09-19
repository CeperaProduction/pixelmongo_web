package ru.pixelmongo.pixelmongo.handlers.impl;

import java.util.Random;

import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenRandomInt;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;

@Component
public class DonatePackRandomIntTokenHandler implements DonatePackTokenHandler<DonatePackTokenRandomInt>{

    private Random random = new Random();

    @Override
    public DonatePackTokenType getTokenType() {
        return DonatePackTokenType.RANDOM_INT;
    }

    @Override
    public HandleResult processToken(DonatePackTokenRandomInt token, Object... data) {
        int result = 0;
        if(token.getMax() >= token.getMin())
            result = token.getMin() + random.nextInt(token.getMax() - token.getMin() + 1);
        return new HandleResult(result+"", 0);
    }

}
