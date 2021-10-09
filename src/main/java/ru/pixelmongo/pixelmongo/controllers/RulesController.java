package ru.pixelmongo.pixelmongo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.pixelmongo.pixelmongo.repositories.primary.RulesRepository;

@Controller
@RequestMapping("/rules")
public class RulesController {

    @Autowired
    private RulesRepository rules;

    @GetMapping
    public String rules(Model model) {
        model.addAttribute("rules", rules.findAll());
        return "rules";
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "rules");
    }

}
