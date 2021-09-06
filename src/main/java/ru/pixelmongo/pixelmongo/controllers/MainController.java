package ru.pixelmongo.pixelmongo.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.pixelmongo.pixelmongo.model.entities.User;
import ru.pixelmongo.pixelmongo.model.entities.UserDetails;
import ru.pixelmongo.pixelmongo.model.entities.forms.UserRegistrationForm;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    private UserService userService;

    //@Autowired
    //private CaptchaService captchaService;

    @GetMapping
    public String index(Model model) {
        User user = getCurrentUser().orElse(new User());
        model.addAttribute("user", user);
        return "main";
    }

    @GetMapping("/register")
    public String register(Model model) {
        //model.addAttribute("captcha_key", captchaService.getPublicKey());
        model.addAttribute("register", new UserRegistrationForm());
        return "register";
    }

    private Optional<User> getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userDetails = auth.getPrincipal();
        if(userDetails instanceof UserDetails) {
            return userService.getUser((UserDetails) userDetails);
        }
        return Optional.empty();
    }

}
