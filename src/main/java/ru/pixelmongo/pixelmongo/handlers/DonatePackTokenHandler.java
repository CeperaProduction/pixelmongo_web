package ru.pixelmongo.pixelmongo.handlers;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackToken;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePackTokenData;

public interface DonatePackTokenHandler<T extends DonatePackToken> {

    public DonatePackTokenType getTokenType();

    public ProcessResult processToken(T token, Object... data);

    public T makeToken(DonatePackTokenData data, DonatePack pack) throws Exception;

    public DonatePackTokenData makeData(T token);

    public static class ProcessResult {

        private final String tokenValue;
        private final int costChange;

        public ProcessResult(String tokenValue, int costChange) {
            this.tokenValue = tokenValue;
            this.costChange = costChange;
        }

        public String getTokenValue() {
            return tokenValue;
        }

        public int getCostChange() {
            return costChange;
        }

    }

}
