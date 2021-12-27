package ru.pixelmongo.pixelmongo.exceptions;

public class PromocodeException extends Exception{

    private static final long serialVersionUID = 7258154969140943640L;

    private final String code;
    private final int promoId;

    public PromocodeException(String message, String code) {
        this(message, 0, code);
    }

    public PromocodeException(String message, int promoId, String code) {
        super(message);
        this.promoId = promoId;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public int getPromoId() {
        return promoId;
    }

}
