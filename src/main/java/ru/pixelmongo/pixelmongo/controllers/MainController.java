package ru.pixelmongo.pixelmongo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping
    public String index(Model model) {
        return "main";
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "main");
    }

}
