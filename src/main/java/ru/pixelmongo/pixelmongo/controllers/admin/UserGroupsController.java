package ru.pixelmongo.pixelmongo.controllers.admin;

import java.util.Locale;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.PopupMessage;
import ru.pixelmongo.pixelmongo.model.entities.User;
import ru.pixelmongo.pixelmongo.model.entities.UserGroup;
import ru.pixelmongo.pixelmongo.model.entities.forms.UserGroupManageForm;
import ru.pixelmongo.pixelmongo.repositories.UserGroupRepository;
import ru.pixelmongo.pixelmongo.repositories.UserPermissionRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;

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
    private User currentUser;

    @Autowired
    private AdminLogService logs;

    @GetMapping
    public String groupList(Model model) {
        model.addAttribute("groups", groups.findAll());
        return "admin/groups";
    }

    @GetMapping("/new")
    public String newGroup(Model model, Locale loc) {
        model.addAttribute("group", new UserGroup());
        model.addAttribute("groupForm", new UserGroupManageForm());
        model.addAttribute("permissions", permissions.findAll());
        return "admin/group";
    }

    @PostMapping("/new")
    public String newGroupSave(
            @ModelAttribute("groupForm") @Valid UserGroupManageForm form,
            BindingResult binding,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            Locale loc) {

        UserGroup group = new UserGroup();
        if(checkForm(form, binding, 0, loc)) {
            form.apply(group);
            group = groups.save(group);
            logs.log("admin.log.group.create",
                    new Object[] {group.getName()+" #"+group.getId()},
                    currentUser, request.getRemoteAddr());
            popupMsg.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.group.created", new Object[] {group.getName()}, loc),
                            PopupMessage.Type.OK),
                    request, response);
            return "redirect:/admin/groups/"+group.getId();
        }
        model.addAttribute("group", group);
        model.addAttribute("permissions", permissions.findAll());
        return "admin/group";
    }

    @GetMapping("/{groupId}")
    public String group(@PathVariable int groupId, Model model, Locale loc) {
        UserGroup group = findGroup(groupId, loc);
        model.addAttribute("group", group);
        model.addAttribute("groupForm", new UserGroupManageForm(group));
        model.addAttribute("permissions", permissions.findAll());
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
        if(checkForm(form, binding, groupId, loc)) {
            form.apply(group);
            group = groups.save(group);
            logs.log("admin.log.group.edit",
                    new Object[] {group.getName()+" #"+group.getId()},
                    currentUser, request.getRemoteAddr());
            popupMsg.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.group.edited", new Object[] {group.getName()}, loc),
                            PopupMessage.Type.OK),
                    request, response);
        }
        model.addAttribute("group", group);
        model.addAttribute("permissions", permissions.findAll());
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
        groups.freeGroup(group);
        groups.delete(group);
        logs.log("admin.log.group.delete",
                new Object[] {group.getName()+" #"+group.getId()},
                currentUser, request.getRemoteAddr());
        popupMsg.sendUsingCookies(
                new PopupMessage(
                        msg.getMessage("admin.group.deleted", new Object[] {group.getName()}, loc),
                        PopupMessage.Type.WARN),
                request, response);
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

}
