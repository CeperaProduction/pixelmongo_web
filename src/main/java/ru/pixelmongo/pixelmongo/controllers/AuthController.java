package ru.pixelmongo.pixelmongo.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.exceptions.InvalidCaptchaException;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmation;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmationType;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.model.dto.forms.PasswordChangeForm;
import ru.pixelmongo.pixelmongo.model.dto.forms.UserRegistrationForm;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.services.CaptchaService;
import ru.pixelmongo.pixelmongo.services.MailedConfirmationService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
public class AuthController {

    public static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private MessageSource msg;

    @Autowired
    private PopupMessageService popup;

    @Autowired
    private MailedConfirmationService confirms;

    @Autowired
    private UserRepository users;

    @Autowired
    private UserService usersService;

    @Autowired
    private CaptchaService captchaService;

    @GetMapping("/register")
    public String register(Model model, Locale loc, HttpServletRequest request,
            HttpServletResponse response) {
        if(isLoggedIn()) {
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("auth.logged.already", null, loc),
                            PopupMessage.Type.INFO),
                    request, response);
            return "redirect:/";
        }
        model.addAttribute("register", new UserRegistrationForm());
        model.addAttribute("mode", "register");
        return "register";
    }

    @GetMapping("/password")
    public String changePassword(Model model, Locale loc, HttpServletRequest request,
            HttpServletResponse response) {

        if(isLoggedIn()) {
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("auth.logged.already", null, loc),
                            PopupMessage.Type.INFO),
                    request, response);
            return "redirect:/";
        }


        model.addAttribute("email", "");
        model.addAttribute("errors", Collections.emptyList());

        return "password_change_email";
    }

    @PostMapping(value = "/password", params = "email")
    public String changePassword(@RequestParam("email") String email,
            @RequestParam(name = "g-recaptcha-response", defaultValue = "") String captcha,
            Model model, Locale loc, HttpServletRequest request,
            HttpServletResponse response) {

        if(isLoggedIn()) {
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("auth.logged.already", null, loc),
                            PopupMessage.Type.INFO),
                    request, response);
            return "redirect:/";
        }

        List<String> errors = new ArrayList<>();
        String ip = request.getRemoteAddr();
        try {
            captchaService.processResponse(captcha, ip);
        }catch(InvalidCaptchaException ex) {
            errors.add(msg.getMessage("captcha.fail", null, loc));
        }
        User user = null;
        if(errors.isEmpty()) {
            user = users.findByEmail(email).orElse(null);
            if(user == null) {
                errors.add(msg.getMessage("mconfirm.passwordchange.not_found", null, loc));
            }
        }
        if(errors.isEmpty()) {
            long await = confirms.checkForSpam(user, MailedConfirmationType.CHANGE_PASSWORD, request);
            if(await > 0) {
                errors.add(msg.getMessage("mconfirm.error.spam", new Object[] {confirms.printAwaitTime(await)}, loc));
            }
        }
        if(errors.isEmpty()) {
            confirms.sendConfirmation(MailedConfirmationType.CHANGE_PASSWORD, user, loc, request);
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("mconfirm.passwordchange.send", null, loc),
                            PopupMessage.Type.INFO),
                    request, response);
            return "redirect:/";
        }

        model.addAttribute("email", email);
        model.addAttribute("errors", errors);

        return "password_change_email";
    }

    @GetMapping(value = "/password", params = "key")
    public String changePassword(@RequestParam("key") String confirmKey, Model model,
            Locale loc, HttpServletRequest request, HttpServletResponse response) {

        if(isLoggedIn()) {
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("auth.logged.already", null, loc),
                            PopupMessage.Type.INFO),
                    request, response);
            return "redirect:/";
        }

        MailedConfirmation confirm = confirms.getActiveConfirmation(confirmKey, MailedConfirmationType.CHANGE_PASSWORD)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        msg.getMessage("mconfirm.error.not_found", null, loc)));
        model.addAttribute("key", confirm.getKey());
        model.addAttribute("passwordForm", new PasswordChangeForm());

        return "password_change";
    }

    @PostMapping(value = "/password", params = "key")
    public String changePassword(@RequestParam("key") String confirmKey,
            @ModelAttribute("passwordForm") @Valid PasswordChangeForm form, BindingResult binding,
            Model model, Locale loc, HttpServletRequest request, HttpServletResponse response) {

        if(isLoggedIn()) {
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("auth.logged.already", null, loc),
                            PopupMessage.Type.INFO),
                    request, response);
            return "redirect:/";
        }

        MailedConfirmation confirm = confirms.getActiveConfirmation(confirmKey, MailedConfirmationType.CHANGE_PASSWORD)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        msg.getMessage("mconfirm.error.not_found", null, loc)));
        if(!binding.hasErrors()) {

            User user = users.findById(confirm.getUserId())
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                            msg.getMessage("error.status.404.user", null, loc)));

            usersService.changePassword(user, form.getPassword(), true);
            confirms.consumeConfirmations(user.getId(), MailedConfirmationType.CHANGE_PASSWORD);

            popup.sendUsingCookies(
                    new PopupMessage(msg.getMessage("mconfirm.passwordchange.done",
                                new Object[] {user.getName()}, loc),
                            PopupMessage.Type.OK),
                    request, response);

            return "redirect:/#login";

        }
        model.addAttribute("key", confirm.getKey());
        return "password_change";
    }

    private boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && !(auth instanceof AnonymousAuthenticationToken);
    }



}
