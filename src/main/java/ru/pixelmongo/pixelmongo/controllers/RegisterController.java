package ru.pixelmongo.pixelmongo.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.model.dto.forms.UserRegistrationForm;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private MessageSource msg;

    @Autowired
    private PopupMessageService popup;

    @GetMapping
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
        return "register";
    }

    private boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && !(auth instanceof AnonymousAuthenticationToken);
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "register");
    }

}
