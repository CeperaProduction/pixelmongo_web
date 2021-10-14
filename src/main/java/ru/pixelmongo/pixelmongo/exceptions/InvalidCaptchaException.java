package ru.pixelmongo.pixelmongo.exceptions;

public class InvalidCaptchaException extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = -7923505220865983309L;

    public InvalidCaptchaException(String message) {
        super(message);
    }

}
