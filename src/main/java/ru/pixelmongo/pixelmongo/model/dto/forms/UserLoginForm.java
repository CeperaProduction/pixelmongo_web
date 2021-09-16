package ru.pixelmongo.pixelmongo.model.dto.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import ru.pixelmongo.pixelmongo.utils.DefaulPatterns;

public class UserLoginForm {

    @NotNull()
    @Pattern(regexp = DefaulPatterns.USERNAME_PATTERN, message = "{auth.login.invalid}")
    private String login;

    @NotNull
    @NotBlank(message = "{auth.password.invalid}")
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
