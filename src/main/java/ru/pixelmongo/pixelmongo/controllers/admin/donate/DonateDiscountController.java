package ru.pixelmongo.pixelmongo.controllers.admin.donate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonatePageRepository;

@Controller
@RequestMapping("/admin/donate/discount")
public class DonateDiscountController {

    @Autowired
    private DonatePageRepository pages;

    @GetMapping
    public String discount(Model model) {

        model.addAttribute("pages", pages.findAllByOrderByOrdinaryAsc());

        return "admin/donate/discount";
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "donate");
    }

}
