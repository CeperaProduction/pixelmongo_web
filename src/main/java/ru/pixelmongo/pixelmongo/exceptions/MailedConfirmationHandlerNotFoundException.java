package ru.pixelmongo.pixelmongo.exceptions;

public class MailedConfirmationHandlerNotFoundException extends RuntimeException{

    private static final long serialVersionUID = 6521362110228523580L;

    private final String handlerType;

    public MailedConfirmationHandlerNotFoundException(String handlerType) {
        super("Mailed confirmation handler with type '"+handlerType+"' not found");
        this.handlerType = handlerType;
    }

    public String getHandlerType() {
        return handlerType;
    }

}
