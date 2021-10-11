package ru.pixelmongo.pixelmongo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.pixelmongo.pixelmongo.repositories.primary.StaffRepository;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private StaffRepository staff;

    @GetMapping
    public String staff(Model model) {
        model.addAttribute("staffs", staff.findAllByOrderByOrdinaryAsc());
        return "staff";
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "staff");
    }

}
