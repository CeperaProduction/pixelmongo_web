package ru.pixelmongo.pixelmongo.exceptions;

public class PromocodeNotFoundException extends PromocodeException{

    private static final long serialVersionUID = 6464107231039425781L;

    public PromocodeNotFoundException(String code) {
        super("Promocode '"+code+"' not found", code);
    }

}
