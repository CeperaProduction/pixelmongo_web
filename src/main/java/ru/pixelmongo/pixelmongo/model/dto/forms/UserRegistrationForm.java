package ru.pixelmongo.pixelmongo.model.dto.forms;

import javax.validation.constraints.Email;

import ru.pixelmongo.pixelmongo.model.dto.ConfirmedPassword;

public class UserRegistrationForm extends UserLoginForm implements ConfirmedPassword {

    private String passwordRepeat;

    @Email(message = "{auth.email.invalid}")
    private String email;

    @Override
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
