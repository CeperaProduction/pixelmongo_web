package ru.pixelmongo.pixelmongo.model.entities.forms;

import javax.validation.constraints.Email;

public class UserRegistrationForm extends UserLoginForm {

    private String passwordRepeat;

    @Email
    private String email;

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
