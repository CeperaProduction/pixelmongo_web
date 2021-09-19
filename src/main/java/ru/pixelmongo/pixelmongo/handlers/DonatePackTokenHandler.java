package ru.pixelmongo.pixelmongo.handlers;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackToken;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;

public interface DonatePackTokenHandler<T extends DonatePackToken> {

    public DonatePackTokenType getTokenType();

    public HandleResult processToken(T token, Object... data);

    public static class HandleResult {

        private final String tokenValue;
        private final int costChange;

        public HandleResult(String tokenValue, int costChange) {
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
