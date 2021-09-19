package ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens;

public enum DonatePackTokenType {
    RANDOM_INT, RANDOM_VALUE, SELECT_VALUE;

    public static class Values{

        public static final String RANDOM_INT = "RANDOM_INT";
        public static final String RANDOM_VALUE = "RANDOM_VALUE";
        public static final String SELECT_VALUE = "SELECT_VALUE";

    }
}
