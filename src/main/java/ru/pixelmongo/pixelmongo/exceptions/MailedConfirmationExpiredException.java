package ru.pixelmongo.pixelmongo.exceptions;

public class MailedConfirmationExpiredException extends RuntimeException{

    private static final long serialVersionUID = 7709626125626194825L;

    public MailedConfirmationExpiredException() {
        super("Mailed confirmation expired");
    }

}
