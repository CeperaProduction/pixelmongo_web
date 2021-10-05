package ru.pixelmongo.pixelmongo.model.dto.forms;

import javax.validation.constraints.Email;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.ConfirmedPassword;

public class UserManageForm implements ConfirmedPassword{

    @Email(message = "{auth.email.invalid}")
    private String email;

    private String password = "";

    private String passwordRepeat = "";

    private String currentPassword = "";

    public UserManageForm() {}

    public UserManageForm(User user) {
        this.email = user.getEmail();
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }


}
