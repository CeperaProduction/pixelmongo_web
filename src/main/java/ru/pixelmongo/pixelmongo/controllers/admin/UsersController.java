package ru.pixelmongo.pixelmongo.controllers.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.exceptions.WrongImageSizeException;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.UserGroup;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.model.dto.forms.SkinUploadForm;
import ru.pixelmongo.pixelmongo.model.dto.forms.UserManageForm;
import ru.pixelmongo.pixelmongo.repositories.primary.UserGroupRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.PlayerSkinService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.TemplateService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/admin/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository users;

    @Autowired
    private UserGroupRepository groups;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private MessageSource msg;

    @Autowired
    private PopupMessageService popupMsg;

    @Autowired
    private AdminLogService logs;

    @Autowired
    private PlayerSkinService skinService;

    @GetMapping
    public String userList(@RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer group,
            Model model) {
        UserGroup userGroup = null;
        if(group != null)
            userGroup = groups.findById(group).orElse(null);
        Pageable pageable = PageRequest.of(page-1, 50);
        Page<User> usersPage = this.users.findAllSorted(search, userGroup, pageable);
        templateService.addPagination(model, page, usersPage.getTotalPages(), 9);

        model.addAttribute("search", StringUtils.hasText(search) ? search : "");
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("users_count", usersPage.getTotalElements());
        model.addAttribute("groups", groups.findAll());
        model.addAttribute("group_selected", userGroup == null ? 0 : userGroup.getId());
        return "admin/users";
    }

    @GetMapping("/{userName}")
    public String user(@PathVariable String userName, Model model, Locale loc) {
        User user = findUser(userName, loc);

        model.addAttribute("user", user);
        model.addAttribute("can_manage", hasManagePerm(user) && canManage(user));
        model.addAttribute("userForm", new UserManageForm(user));
        model.addAttribute("skinForm", new SkinUploadForm());
        model.addAttribute("groups", getAvailableGroups(user));

        return "admin/user";
    }

    @PostMapping("/{userName}")
    public String handlePost(@PathVariable("userName") String userName,
            @RequestParam("target") String target,
            @ModelAttribute("userForm") @Valid UserManageForm userForm,
            BindingResult binding1,
            @ModelAttribute("skinForm") @Valid SkinUploadForm skinForm,
            BindingResult binding2,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            Locale loc) {

        switch(target) {
        case "user":
            return userSave(userName, userForm, binding1, model, request, response, loc);
        case "skin": case "cape":
            return saveSkin(userName, skinForm, binding2, model, request, response, loc);

        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null);
    }

    @DeleteMapping("/{userName}")
    public String handleDelete(@PathVariable("userName") String userName,
            @RequestParam("target") String target,
            HttpServletRequest request,
            HttpServletResponse response,
            Locale loc) {

        switch(target) {
        case "user":
            return userDelete(userName, request, response, loc);
        case "skin": case "cape":
            return deleteSkin(userName, target, request, response, loc);

        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null);
    }

    public String userSave(String userName,
            UserManageForm userForm,
            BindingResult binding,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            Locale loc) {

        User user = findUser(userName, loc);
        checkPerms(user);

        checkForm(userForm, user, binding, loc);

        if(!binding.hasErrors()) {
            boolean changed = false;

            if(StringUtils.hasText(userForm.getEmail())
                    && !userForm.getEmail().equals(user.getEmail())) {
                user.setEmail(userForm.getEmail());
                changed = true;
            }

            if(StringUtils.hasText(userForm.getPassword())) {
                userService.changePassword(user, userForm.getPassword());
                changed = true;
            }

            if(user.getGroup().getId() != userForm.getGroupId()) {
                groups.findById(userForm.getGroupId())
                    .filter(g->{
                        UserGroup cg = userService.getCurrentUser().getGroup();
                        if(cg.getId() == UserGroupRepository.GROUP_ID_ADMIN)
                            return true;
                        return cg.getPermissionLevel() > g.getPermissionLevel();
                    })
                    .ifPresent(user::setGroup);
                changed = true;
            }

            if(user.hasCape() != userForm.isHasCape()) {
                user.setHasCape(userForm.isHasCape());
                changed = true;
            }

            if(user.hasHDSkin() != userForm.isHasHDSkin()) {
                user.setHasHDSkin(userForm.isHasHDSkin());
                changed = true;
            }

            if(changed) {
                user = users.save(user);
                logs.log("admin.log.user.edit",
                        new Object[] {user.getName()+" #"+user.getId()},
                        userService.getCurrentUser(), request.getRemoteAddr());
                popupMsg.sendUsingCookies(
                        new PopupMessage(
                                msg.getMessage("admin.user.edited", new Object[] {user.getName()}, loc),
                                PopupMessage.Type.OK),
                        request, response);
            }

        }

        model.addAttribute("user", user);
        model.addAttribute("can_manage", hasManagePerm(user) && canManage(user));
        model.addAttribute("groups", getAvailableGroups(user));
        model.addAttribute("skinForm", new SkinUploadForm());

        return "admin/user";
    }

    public String userDelete(String userName,
            HttpServletRequest request,
            HttpServletResponse response,
            Locale loc) {
        User user = findUser(userName, loc);
        checkPerms(user);
        if(user.getId() == userService.getCurrentUser().getId()) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED,
                    msg.getMessage("error.status.405.user", null, loc));
        }
        users.delete(user);
        logs.log("admin.log.user.delete",
                new Object[] {user.getName()+" #"+user.getId()},
                userService.getCurrentUser(), request.getRemoteAddr());
        popupMsg.sendUsingCookies(
                new PopupMessage(
                        msg.getMessage("admin.user.deleted", new Object[] {user.getName()}, loc),
                        PopupMessage.Type.WARN),
                request, response);
        return "redirect:/admin/users";
    }

    //SKIN MANAGEMENT

    public String saveSkin(String userName,
            SkinUploadForm skinForm,
            BindingResult binding,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            Locale loc) {

        User user = findUser(userName, loc);
        checkPerms(user);

        if(!binding.hasErrors()) {

            if(!skinForm.getSkin().isEmpty()) {
                try {
                    skinService.uploadSkin(skinForm.getSkin(), user, true);

                    logs.log("admin.log.user.skin.change",
                            new Object[] {user.getName()+" #"+user.getId()},
                            userService.getCurrentUser(), request.getRemoteAddr());
                    popupMsg.sendUsingCookies(
                            new PopupMessage(msg.getMessage("skin.skin.changed", null, loc),
                                    PopupMessage.Type.OK), request, response);

                }catch(WrongImageSizeException ex) {
                    binding.addError(new FieldError("skinForm", "skin",
                            msg.getMessage("skin.skin.wrong_size", null, loc)));
                }
            }

            if(!skinForm.getCape().isEmpty()) {
                try {
                    skinService.uploadCape(skinForm.getCape(), user, true);

                    logs.log("admin.log.user.cape.change",
                            new Object[] {user.getName()+" #"+user.getId()},
                            userService.getCurrentUser(), request.getRemoteAddr());
                    popupMsg.sendUsingCookies(
                            new PopupMessage(msg.getMessage("skin.cape.changed", null, loc),
                                    PopupMessage.Type.OK), request, response);

                }catch(WrongImageSizeException ex) {
                    binding.addError(new FieldError("skinForm", "cape",
                            msg.getMessage("skin.cape.wrong_size", null, loc)));
                }
            }

        }

        model.addAttribute("user", user);
        model.addAttribute("can_manage", hasManagePerm(user) && canManage(user));
        model.addAttribute("groups", getAvailableGroups(user));
        model.addAttribute("userForm", new UserManageForm(user));

        return "admin/user";
    }

    public String deleteSkin(@PathVariable String userName,
            String target,
            HttpServletRequest request,
            HttpServletResponse response,
            Locale loc) {
        User user = findUser(userName, loc);
        checkPerms(user);

        switch(target) {
        case "skin" :
            if(skinService.deleteSkin(user)) {
                logs.log("admin.log.user.skin.change",
                        new Object[] {user.getName()+" #"+user.getId()},
                        userService.getCurrentUser(), request.getRemoteAddr());
                popupMsg.sendUsingCookies(
                        new PopupMessage(msg.getMessage("skin.skin.deleted", null, loc),
                                PopupMessage.Type.WARN), request, response);
            }
            break;
        case "cape" :
            if(skinService.deleteCape(user)) {
                logs.log("admin.log.user.cape.change",
                        new Object[] {user.getName()+" #"+user.getId()},
                        userService.getCurrentUser(), request.getRemoteAddr());
                popupMsg.sendUsingCookies(
                        new PopupMessage(msg.getMessage("skin.cape.deleted", null, loc),
                                PopupMessage.Type.WARN), request, response);
            }
            break;
        default :
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null);
        }

        return "redirect:/admin/users/"+user.getName();
    }



    private User findUser(String userName, Locale loc) {
        return users.findByName(userName).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.user", null, loc)));
    }

    private void checkForm(UserManageForm form,
            User user,
            BindingResult binding,
            Locale loc) {
        if(!form.getPassword().equals(form.getPasswordRepeat())) {
            binding.addError(new FieldError("userForm", "passwordRepeat",
                    msg.getMessage("auth.password.not_same", null, loc)));
        }else if(users.findByEmail(form.getEmail())
                .filter(u->u.getId() != user.getId()).isPresent()) {
            binding.addError(new FieldError("userForm", "email",
                    msg.getMessage("admin.user.email.busy", null, loc)));
        }
    }

    private void checkPerms(User targetUser) {
        checkPerm(targetUser);
        checkPermLevel(targetUser);
    }

    private void checkPermLevel(User targetUser) {
        if(!canManage(targetUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private void checkPerm(User targetUser) {
        if(!hasManagePerm(targetUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private boolean canManage(User targetUser) {
        User currentUser = this.userService.getCurrentUser();
        UserGroup currentGroup = currentUser.getGroup();
        return currentUser.getId() == targetUser.getId()
                || currentGroup.getId() == UserGroupRepository.GROUP_ID_ADMIN
                || targetUser.getGroup().getPermissionLevel() < currentGroup.getPermissionLevel();
    }

    private boolean hasManagePerm(User target) {
        return target.getId() == userService.getCurrentUser().getId()
                || userService.hasPerm("admin.panel.users.edit");
    }

    private List<UserGroup> getAvailableGroups(User target){
        ArrayList<UserGroup> av = new ArrayList<>();
        UserGroup currentGroup = userService.getCurrentUser().getGroup();
        if(currentGroup.getId() == UserGroupRepository.GROUP_ID_ADMIN) {
            groups.findAll().forEach(av::add);
        }else {
            for(UserGroup g : groups.findAll())
                if(currentGroup.getPermissionLevel() > g.getPermissionLevel())
                    av.add(g);
            if(!av.stream().anyMatch(g->g.getId() == target.getGroup().getId())) {
                av.add(0, target.getGroup());
            }
        }
        return av;
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "users");
    }

}
