package ru.pixelmongo.pixelmongo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RatingHandlerDisabledException extends ResponseStatusException {

    private static final long serialVersionUID = -3407311131190095971L;

    private final String handlerName;

    public RatingHandlerDisabledException(String handlerName) {
        super(HttpStatus.FORBIDDEN, "Rating handler '"+handlerName+"' is disabled.");
        this.handlerName = handlerName;
    }

    public String getHandlerName() {
        return handlerName;
    }

}
