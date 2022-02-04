package ru.pixelmongo.pixelmongo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RatingHandlerNotFoundException extends ResponseStatusException{

    private static final long serialVersionUID = -3414209599325675059L;

    private final String handlerName;

    public RatingHandlerNotFoundException(String handlerName) {
        super(HttpStatus.NOT_FOUND, "Rating handler with name '"+handlerName+"' not found");
        this.handlerName = handlerName;
    }

    public String getHandlerName() {
        return handlerName;
    }

}
