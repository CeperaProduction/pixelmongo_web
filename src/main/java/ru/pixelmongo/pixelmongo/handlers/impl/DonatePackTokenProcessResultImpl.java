package ru.pixelmongo.pixelmongo.handlers.impl;

import ru.pixelmongo.pixelmongo.handlers.DonatePackTokenProcessResult;

public class DonatePackTokenProcessResultImpl implements DonatePackTokenProcessResult{

    private final String tokenValue;
    private final int costChange;

    public DonatePackTokenProcessResultImpl(String tokenValue, int costChange) {
        this.tokenValue = tokenValue;
        this.costChange = costChange;
    }

    @Override
    public String getTokenValue() {
        return tokenValue;
    }

    @Override
    public int getCostChange() {
        return costChange;
    }

}
