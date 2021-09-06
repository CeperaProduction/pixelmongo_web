package ru.pixelmongo.pixelmongo.exceptions;

public class InvalidCaptchaEcxeption extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = -7923505220865983309L;

    public InvalidCaptchaEcxeption(String message) {
        super(message);
    }

}
