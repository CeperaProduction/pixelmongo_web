package ru.pixelmongo.pixelmongo.model.dto.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import ru.pixelmongo.pixelmongo.model.dto.ConfirmedPassword;

public class PasswordChangeForm implements ConfirmedPassword{

    @NotNull
    @NotBlank(message = "{auth.password.invalid}")
    private String password;

    private String passwordRepeat;

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

}
