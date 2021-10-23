package ru.pixelmongo.pixelmongo.exceptions;

import org.springframework.security.core.AuthenticationException;

public class TooMuchLoginAttemptsException extends AuthenticationException {

    private static final long serialVersionUID = 3744623976752938812L;

    public TooMuchLoginAttemptsException() {
        super("Too much failed login attempts. Client blocked.");
    }

    public TooMuchLoginAttemptsException(String message) {
        super(message);
    }

}
