package ru.pixelmongo.pixelmongo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BillingHandlerDisabledException extends ResponseStatusException {

    private static final long serialVersionUID = 3640750587652657918L;

    private final String handlerName;

    public BillingHandlerDisabledException(String handlerName) {
        super(HttpStatus.FORBIDDEN, "Billing handler '"+handlerName+"' is disabled.");
        this.handlerName = handlerName;
    }

    public String getHandlerName() {
        return handlerName;
    }

}
