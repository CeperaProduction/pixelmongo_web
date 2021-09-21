package ru.pixelmongo.pixelmongo.exceptions;

public class DonatePackTokenFormatException extends RuntimeException{

    private static final long serialVersionUID = 8017022464444842297L;

    private final String tokenName;

    public DonatePackTokenFormatException(String tokenName, Exception ex) {
        super("Failed to format token data. Token: "+tokenName, ex);
        this.tokenName = tokenName;
    }

    public String getTokenName() {
        return tokenName;
    }

}
