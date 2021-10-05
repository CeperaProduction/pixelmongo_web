package ru.pixelmongo.pixelmongo.model.dto.forms;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;

public class UserManageAdminForm extends UserManageForm{

    private Integer groupId;

    private boolean hasCape;

    private boolean hasHDSkin;

    public UserManageAdminForm() {}

    public UserManageAdminForm(User user) {
        super(user);
        this.groupId = user.getGroup().getId();
        this.hasCape = user.hasCape();
        this.hasHDSkin = user.hasHDSkin();
    }

    public Integer getGroupId() {
        return groupId;
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
