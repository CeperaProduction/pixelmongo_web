package ru.pixelmongo.pixelmongo.model.dto.forms;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import ru.pixelmongo.pixelmongo.model.dao.primary.UserGroup;
import ru.pixelmongo.pixelmongo.model.dao.primary.UserPermission;
import ru.pixelmongo.pixelmongo.repositories.primary.UserGroupRepository;

public class UserGroupManageForm {

    @NotBlank
    private String name = "";
    private int permLevel = 1;
    private Set<Integer> permissions = new HashSet<>();

    public UserGroupManageForm() {}

    public UserGroupManageForm(UserGroup group) {
        name = group.getName();
        if(group.getId() == UserGroupRepository.GROUP_ID_ADMIN) {
            permLevel = 100;
        }else {
            permLevel = group.getPermissionLevel();
        }
        group.getPermissions().forEach(p->this.permissions.add(p.getId()));
    }

    public void apply(UserGroup group, Set<UserPermission> availablePerms, int maxPermLevel) {
        group.setName(this.name);
        if(group.getId() != UserGroupRepository.GROUP_ID_ADMIN) {
            group.setPermissionLevel(Math.min(permLevel, maxPermLevel));
            for(UserPermission perm : availablePerms) {
                if(this.permissions.contains(perm.getId())) {
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

    public Set<Integer> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Integer> permissions) {
        this.permissions = permissions;
    }

    public int getPermLevel() {
        return permLevel;
    }

    public void setPermLevel(int permLevel) {
        this.permLevel = permLevel;
    }

}
