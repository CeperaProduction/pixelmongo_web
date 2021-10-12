package ru.pixelmongo.pixelmongo.handlers.impl.donate;

import java.util.List;

import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenHandler;
import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenProcessResult;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenRandomValue;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePackTokenData;
import ru.pixelmongo.pixelmongo.utils.RandomUtils;

@Component
public class DonatePackRandomValueTokenHandler implements DonatePackTokenHandler<DonatePackTokenRandomValue>{

    @Override
    public DonatePackTokenType getTokenType() {
        return DonatePackTokenType.RANDOM_VALUE;
    }

    @Override
    public DonatePackTokenProcessResult processToken(DonatePackTokenRandomValue token, List<String> data) throws Exception {
        String result = RandomUtils.randomElement(token.getValues(), "");
        return new DonatePackTokenProcessResultImpl(result, 0);
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
