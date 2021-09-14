package ru.pixelmongo.pixelmongo.model.dto.forms;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import ru.pixelmongo.pixelmongo.model.dao.UserGroup;
import ru.pixelmongo.pixelmongo.model.dao.UserPermission;
import ru.pixelmongo.pixelmongo.repositories.UserGroupRepository;

public class UserGroupManageForm {

    @NotBlank
    private String name = "";
    private int permLevel = 1;
    private Set<UserPermission> permissions = Collections.emptySet();

    public UserGroupManageForm() {}

    public UserGroupManageForm(UserGroup group) {
        name = group.getName();
        if(group.getId() == UserGroupRepository.GROUP_ID_ADMIN) {
            permLevel = 100;
        }else {
            permLevel = group.getPermissionLevel();
        }
        permissions = new HashSet<>(group.getPermissions());
    }

    public void apply(UserGroup group, Set<UserPermission> availablePerms, int maxPermLevel) {
        group.setName(this.name);
        if(group.getId() != UserGroupRepository.GROUP_ID_ADMIN) {
            group.setPermissionLevel(Math.min(permLevel, maxPermLevel));
            for(UserPermission perm : availablePerms) {
                if(this.permissions.contains(perm)) {
                    group.getPermissions().add(perm);
                }else {
                    group.getPermissions().remove(perm);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public int getPermLevel() {
        return permLevel;
    }

    public void setPermLevel(int permLevel) {
        this.permLevel = permLevel;
    }

}
