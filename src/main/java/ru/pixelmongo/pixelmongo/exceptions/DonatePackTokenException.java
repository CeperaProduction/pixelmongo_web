package ru.pixelmongo.pixelmongo.exceptions;

public class DonatePackTokenException extends RuntimeException{

    private static final long serialVersionUID = -1178866781486486232L;

    private final String tokenName;

    public DonatePackTokenException(String message, String tokenName, Exception nestedException) {
        super(message, nestedException);
        this.tokenName = tokenName;
    }

    public String getTokenName() {
        return tokenName;
    }

}
