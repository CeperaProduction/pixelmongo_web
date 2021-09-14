package ru.pixelmongo.pixelmongo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.pixelmongo.pixelmongo.model.dto.forms.UserRegistrationForm;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping
    public String index(Model model) {
        return "main";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("register", new UserRegistrationForm());
        return "register";
    }

}
