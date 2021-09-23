package ru.pixelmongo.pixelmongo.exceptions;

public class DonatePackTokenFormatException extends DonatePackTokenException{

    private static final long serialVersionUID = 8017022464444842297L;

    public DonatePackTokenFormatException(String tokenName, Exception ex) {
        super("Failed to format token data. Token: "+tokenName, tokenName, ex);
    }

}
