package ru.pixelmongo.pixelmongo.model.dto.forms;

import javax.validation.constraints.Email;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;

public class UserManageForm{

    @Email(message = "{auth.email.invalid}")
    private String email;

    private String password = "";

    private String passwordRepeat = "";

    private Integer groupId;

    private boolean hasCape;

    private boolean hasHDSkin;

    public UserManageForm() {}

    public UserManageForm(User user) {
        this.email = user.getEmail();
        this.groupId = user.getGroup().getId();
        this.hasCape = user.hasCape();
        this.hasHDSkin = user.hasHDSkin();
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

    public boolean isHasCape() {
        return hasCape;
    }

    public boolean isHasHDSkin() {
        return hasHDSkin;
    }

    public void setHasCape(boolean hasCape) {
        this.hasCape = hasCape;
    }

    public void setHasHDSkin(boolean hasHDSkin) {
        this.hasHDSkin = hasHDSkin;
    }


}
