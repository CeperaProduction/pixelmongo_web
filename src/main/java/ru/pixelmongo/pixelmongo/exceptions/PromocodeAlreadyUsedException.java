package ru.pixelmongo.pixelmongo.exceptions;

public class PromocodeAlreadyUsedException extends PromocodeException{

    private static final long serialVersionUID = -8180377006854506358L;

    public PromocodeAlreadyUsedException(int promoId, String code) {
        super("Promocode already used", promoId, code);
    }

}
