package ru.pixelmongo.pixelmongo.model.entities.forms;

import javax.validation.constraints.Email;

import ru.pixelmongo.pixelmongo.model.entities.User;

public class UserManageForm{

    @Email(message = "{auth.email.invalid}")
    private String email;

    private String password = "";

    private String passwordRepeat = "";

    private Integer groupId;

    public UserManageForm() {}

    public UserManageForm(User user) {
        this.email = user.getEmail();
        this.groupId = user.getGroup().getId();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public Integer getGroupId() {
        return groupId;
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

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

}
