package ru.pixelmongo.pixelmongo.controllers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.pixelmongo.pixelmongo.model.dto.forms.UserRegistrationForm;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @GetMapping
    public String register(Model model) {
        if(isLoggedIn())
            return "redirect:/";
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
