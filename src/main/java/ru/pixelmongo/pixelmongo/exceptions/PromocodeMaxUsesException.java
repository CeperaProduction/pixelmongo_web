package ru.pixelmongo.pixelmongo.exceptions;

public class PromocodeMaxUsesException extends PromocodeException{

    private static final long serialVersionUID = -8180377006854506358L;

    public PromocodeMaxUsesException(int promoId, String code) {
        super("Promocode used too much times", promoId, code);
    }

}
