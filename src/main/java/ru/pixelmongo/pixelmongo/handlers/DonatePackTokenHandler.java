package ru.pixelmongo.pixelmongo.handlers;

import java.util.List;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackToken;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePackTokenData;

public interface DonatePackTokenHandler<T extends DonatePackToken> {

    public DonatePackTokenType getTokenType();

    public DonatePackTokenProcessResult processToken(T token, List<String> data) throws Exception;

    public T makeToken(DonatePackTokenData data, DonatePack pack) throws Exception;

    public DonatePackTokenData makeData(T token);

}
