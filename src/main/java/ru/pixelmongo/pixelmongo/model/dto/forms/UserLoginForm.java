package ru.pixelmongo.pixelmongo.model.dto.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UserLoginForm {

    @NotNull()
    @Pattern(regexp = "[a-zA-Z0-9_-]{3,16}", message = "{auth.login.invalid}")
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
