package ru.pixelmongo.pixelmongo.controllers.admin;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.primary.UserGroup;
import ru.pixelmongo.pixelmongo.model.dao.primary.UserPermission;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.model.dto.forms.UserGroupManageForm;
import ru.pixelmongo.pixelmongo.repositories.primary.UserGroupRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.UserPermissionRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/admin/groups")
public class UserGroupsController {

    @Autowired
    private UserGroupRepository groups;

    @Autowired
    private UserPermissionRepository permissions;

    @Autowired
    private MessageSource msg;

    @Autowired
    private PopupMessageService popupMsg;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminLogService logs;

    @GetMapping
    public String groupList(Model model) {
        model.addAttribute("groups", groups.findAll());
        return "admin/groups";
    }

    @GetMapping("/new")
    public String newGroup(Model model, Locale loc) {
        model.addAttribute("method", "put");
        model.addAttribute("group", new UserGroup());
        model.addAttribute("groupForm", new UserGroupManageForm());
        model.addAttribute("can_manage", true);
        model.addAttribute("permissions", permissions.findAllSorted());
        model.addAttribute("own_permissions", getAvilablePermissions());
        model.addAttribute("max_perm_level", getMaxPermLevel());
        return "admin/group";
    }

    @PutMapping("/new")
    public String newGroupSave(
            @ModelAttribute("groupForm") @Valid UserGroupManageForm form,
            BindingResult binding,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            Locale loc) {

        UserGroup group = new UserGroup();
        Set<UserPermission> avPerms = getAvilablePermissions();
        int permLevel = getMaxPermLevel();
        if(checkForm(form, binding, 0, loc)) {
            form.apply(group, avPerms, permLevel);
            group = groups.save(group);
            logs.log("admin.log.group.create",
                    new Object[] {group.getName()+" #"+group.getId()},
                    userService.getCurrentUser(), request.getRemoteAddr());
            popupMsg.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.group.created", new Object[] {group.getName()}, loc),
                            PopupMessage.Type.OK),
                    request, response);
            return "redirect:/admin/groups/"+group.getId();
        }
        model.addAttribute("method", "put");
        model.addAttribute("group", group);
        model.addAttribute("can_manage", true);
        model.addAttribute("permissions", permissions.findAllSorted());
        model.addAttribute("own_permissions", avPerms);
        model.addAttribute("max_perm_level", permLevel-1);
        return "admin/group";
    }

    @GetMapping("/{groupId}")
    public String group(@PathVariable int groupId, Model model, Locale loc) {
        UserGroup group = findGroup(groupId, loc);
        model.addAttribute("method", "post");
        model.addAttribute("group", group);
        model.addAttribute("can_manage", hasManagePerm() && canManage(group));
        model.addAttribute("groupForm", new UserGroupManageForm(group));
        model.addAttribute("permissions", permissions.findAllSorted());
        model.addAttribute("own_permissions", getAvilablePermissions());
        model.addAttribute("max_perm_level", getMaxPermLevel());
        return "admin/group";
    }

    @PostMapping("/{groupId}")
    public String groupSave(@PathVariable int groupId,
            @ModelAttribute("groupForm") @Valid UserGroupManageForm form,
            BindingResult binding,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            Locale loc) {
        UserGroup group = findGroup(groupId, loc);
        checkPermLevel(group);
        Set<UserPermission> avPerms = getAvilablePermissions();
        int permLevel = getMaxPermLevel();
        if(checkForm(form, binding, groupId, loc)) {
            form.apply(group, avPerms, permLevel);
            group = groups.save(group);
            logs.log("admin.log.group.edit",
                    new Object[] {group.getName()+" #"+group.getId()},
                    userService.getCurrentUser(), request.getRemoteAddr());
            popupMsg.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.group.edited", new Object[] {group.getName()}, loc),
                            PopupMessage.Type.OK),
                    request, response);
            userService.invalidateDetails(group);
        }
        model.addAttribute("method", "post");
        model.addAttribute("group", group);
        model.addAttribute("can_manage", canManage(group));
        model.addAttribute("permissions", permissions.findAllSorted());
        model.addAttribute("own_permissions", avPerms);
        model.addAttribute("max_perm_level", permLevel);
        return "admin/group";
    }

    @DeleteMapping("/{groupId}")
    public String groupDelete(@PathVariable int groupId,
            HttpServletRequest request,
            HttpServletResponse response,
            Locale loc) {
        if(groupId == UserGroupRepository.GROUP_ID_ADMIN
                || groupId == UserGroupRepository.GROUP_ID_USER) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED,
                    msg.getMessage("error.status.405.group", null, loc));
        }
        UserGroup group = findGroup(groupId, loc);
        checkPermLevel(group);
        groups.freeGroup(group);
        groups.delete(group);
        logs.log("admin.log.group.delete",
                new Object[] {group.getName()+" #"+group.getId()},
                userService.getCurrentUser(), request.getRemoteAddr());
        popupMsg.sendUsingCookies(
                new PopupMessage(
                        msg.getMessage("admin.group.deleted", new Object[] {group.getName()}, loc),
                        PopupMessage.Type.WARN),
                request, response);
        userService.invalidateDetails(group);
        return "redirect:/admin/groups";
    }

    private UserGroup findGroup(int id, Locale loc) {
        return groups.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.group", null, loc)));
    }

    private boolean checkForm(UserGroupManageForm form, BindingResult binding,
            int groupId, Locale loc) {
        groups.findByName(form.getName())
            .filter(g->g.getId() != groupId)
            .ifPresent(g->binding.addError(new FieldError("groupForm", "name",
                        msg.getMessage("admin.group.name.busy", null, loc))));
        return !binding.hasErrors();
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "users");
    }

    private Set<UserPermission> getAvilablePermissions(){
        UserGroup currentGroup = userService.getCurrentUser().getGroup();
        if(currentGroup.getId() == UserGroupRepository.GROUP_ID_ADMIN) {
            Set<UserPermission> result = new HashSet<UserPermission>();
            permissions.findAll().forEach(result::add);
            return result;
        }
        return new HashSet<>(currentGroup.getPermissions());
    }

    private boolean canManage(UserGroup targetGroup) {
        UserGroup currentGroup = userService.getCurrentUser().getGroup();
        return currentGroup.getId() == UserGroupRepository.GROUP_ID_ADMIN
                || targetGroup.getPermissionLevel() < currentGroup.getPermissionLevel();
    }

    private void checkPermLevel(UserGroup targetGroup) {
        if(!canManage(targetGroup)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private int getMaxPermLevel() {
        UserGroup currentGroup = userService.getCurrentUser().getGroup();
        if(currentGroup.getId() == UserGroupRepository.GROUP_ID_ADMIN) {
            return 99;
        }
        return currentGroup.getPermissionLevel()-1;
    }

    private boolean hasManagePerm() {
        return userService.hasPerm("admin.panel.groups.edit");
    }

}
