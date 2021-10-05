package ru.pixelmongo.pixelmongo.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.handlers.impl.DonateExtraUnbanHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.model.dto.forms.UserManageForm;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.repositories.sub.PlayerBanRecordRepository;
import ru.pixelmongo.pixelmongo.services.DonateService;
import ru.pixelmongo.pixelmongo.services.PlayerSkinService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PlayerSkinService skinService;

    @Autowired
    private PlayerBanRecordRepository banlist;

    @Autowired
    private DonateService donate;

    @Autowired
    private UserRepository users;

    @Autowired
    private PopupMessageService popupMsg;

    @Autowired
    private MessageSource msg;

    @GetMapping
    public String profile(Model model) {
        User user = userService.getCurrentUser();
        if(user.isAnonymous())
            return "redirect:/";
        addProfileAttributes(user, model);
        model.addAttribute("userForm", new UserManageForm(user));
        return "profile";
    }

    @PostMapping
    public String updateProfile(@ModelAttribute("userForm") @Valid UserManageForm userForm,
            BindingResult binding, Model model, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        User user = userService.getCurrentUser();
        if(user.isAnonymous())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        checkForm(userForm, user, binding, loc);

        if(!binding.hasErrors()) {

            user = getStoredUser(user);

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

            if(changed) {
                user = users.save(user);
                popupMsg.sendUsingCookies(new PopupMessage(msg.getMessage("user.edited", null, loc),
                                PopupMessage.Type.OK), request, response);
            }

            return "redirect:/profile";
        }
        addProfileAttributes(user, model);
        return "profile";
    }

    public void addProfileAttributes(User user, Model model) {
        model.addAttribute("user", user);
        String skinUrl = skinService.getSkinUrlPath(user);
        model.addAttribute("skin", skinUrl);
        model.addAttribute("hasSkin", !skinUrl.startsWith("/img/"));
        model.addAttribute("hdSkinCost", donate.getExtraHandler("hd_skin").getCost(user));
        String capeUrl = skinService.getCapeUrlPath(user);
        model.addAttribute("cape", capeUrl);
        model.addAttribute("hasCape", !capeUrl.startsWith("/img/"));
        model.addAttribute("capeCost", donate.getExtraHandler("cape").getCost(user));
        model.addAttribute("ban", banlist.getActiveBan(user).orElse(null));
        DonateExtraUnbanHandler unbanHandler = donate.getExtraHandler("unban");
        model.addAttribute("unbanCost", unbanHandler.getCost(user));
        model.addAttribute("unbanDayCost", unbanHandler.getDayCost());
        model.addAttribute("unbanMaxCost", unbanHandler.getMaxCost());
    }

    private void checkForm(UserManageForm form, User user, BindingResult binding, Locale loc) {
        if(StringUtils.hasText(form.getEmail())
                &&  users.findByEmail(form.getEmail())
                        .filter(u->u.getId() != user.getId()).isPresent()) {
            binding.addError(new FieldError("userForm", "email",
                    msg.getMessage("user.email.busy", null, loc)));
        }
        if(!StringUtils.hasText(form.getCurrentPassword())
                || !userService.checkPassword(user, form.getCurrentPassword())) {
            binding.addError(new FieldError("userForm", "currentPassword",
                    msg.getMessage("auth.password.wrong", null, loc)));
        }
    }

    private User getStoredUser(User user) {
        if(user.getClass() == User.class) return user;
        return users.getById(user.getId());
    }

}
