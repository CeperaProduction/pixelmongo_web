package ru.pixelmongo.pixelmongo.exceptions;

public class MailedConfirmationNotFoundException extends RuntimeException{

    private static final long serialVersionUID = -6187768344962421974L;

    public MailedConfirmationNotFoundException() {
        super("Mailed confirmation not found");
    }

}
