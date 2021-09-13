package ru.pixelmongo.pixelmongo.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String index() {
        return "admin/main";
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "main");
    }

}
