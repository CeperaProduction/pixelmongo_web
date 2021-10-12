package ru.pixelmongo.pixelmongo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BillingHandlerNotFoundException extends ResponseStatusException{

    private static final long serialVersionUID = -5733578582158503371L;

    private final String handlerName;

    public BillingHandlerNotFoundException(String handlerName) {
        super(HttpStatus.NOT_FOUND, "Billing handler with name '"+handlerName+"' not found");
        this.handlerName = handlerName;
    }

    public String getHandlerName() {
        return handlerName;
    }

}
