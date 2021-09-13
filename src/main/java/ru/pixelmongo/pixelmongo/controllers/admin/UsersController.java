package ru.pixelmongo.pixelmongo.controllers.admin;

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

import ru.pixelmongo.pixelmongo.model.PopupMessage;
import ru.pixelmongo.pixelmongo.model.entities.User;
import ru.pixelmongo.pixelmongo.model.entities.UserGroup;
import ru.pixelmongo.pixelmongo.model.entities.forms.UserManageForm;
import ru.pixelmongo.pixelmongo.repositories.UserGroupRepository;
import ru.pixelmongo.pixelmongo.repositories.UserRepository;
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
    private User currentUser;

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
        if(!model.containsAttribute("userForm")) {
            model.addAttribute("userForm", new UserManageForm(user));
        }

        model.addAttribute("groups", groups.findAll());

        return "admin/user";
    }

    @PostMapping("/{userName}")
    public String userSave(@PathVariable String userName,
            @ModelAttribute("userForm") @Valid UserManageForm userForm,
            BindingResult binding,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            Locale loc) {

        if(!userForm.getPassword().equals(userForm.getPasswordRepeat())) {
            binding.addError(new FieldError("userForm", "passwordRepeat",
                    msg.getMessage("auth.password.not_same", null, loc)));
        }

        if(!binding.hasErrors()) {

            User user = findUser(userName, loc);
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
                    .ifPresent(user::setGroup);
                changed = true;
            }

            if(changed) {
                users.save(user);
                popupMsg.sendUsingCookies(
                        new PopupMessage(
                                msg.getMessage("admin.user.edited", new Object[] {user.getName()}, loc),
                                PopupMessage.Type.OK),
                        request, response);
            }

        }

        return user(userName, model, loc);
    }

    @DeleteMapping("/{userName}")
    public String userDelete(@PathVariable String userName,
            HttpServletRequest request,
            HttpServletResponse response,
            Locale loc) {
        User user = findUser(userName, loc);
        if(user.getId() == currentUser.getId()) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED,
                    msg.getMessage("error.status.405.user", null, loc));
        }
        users.delete(user);
        popupMsg.sendUsingCookies(
                new PopupMessage(
                        msg.getMessage("admin.user.deleted", new Object[] {user.getName()}, loc),
                        PopupMessage.Type.WARN),
                request, response);
        return "redirect:/admin/users";
    }

    private User findUser(String userName, Locale loc) {
        return users.findByName(userName).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.user", null, loc)));
    }

}
