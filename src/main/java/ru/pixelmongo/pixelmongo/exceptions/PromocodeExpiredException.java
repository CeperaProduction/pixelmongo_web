package ru.pixelmongo.pixelmongo.exceptions;

public class PromocodeExpiredException extends PromocodeException{

    private static final long serialVersionUID = -8180377006854506358L;

    public PromocodeExpiredException(int promoId, String code) {
        super("Promocode expired", promoId, code);
    }

}
