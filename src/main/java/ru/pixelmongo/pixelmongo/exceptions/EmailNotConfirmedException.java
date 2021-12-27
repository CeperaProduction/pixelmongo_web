package ru.pixelmongo.pixelmongo.exceptions;

public class EmailNotConfirmedException extends Exception {

    private static final long serialVersionUID = 5316516099155514675L;

    private int userId;

    private String userName;

    private String email;

    public EmailNotConfirmedException(int userId, String userName, String email) {
        super(userName+"'s email "+email+" is not confirmed");
        this.userId = userId;
        this.userName = userName;
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

}
