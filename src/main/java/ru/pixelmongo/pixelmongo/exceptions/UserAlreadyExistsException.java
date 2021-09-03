package ru.pixelmongo.pixelmongo.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 6393193785421833329L;
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }

}
