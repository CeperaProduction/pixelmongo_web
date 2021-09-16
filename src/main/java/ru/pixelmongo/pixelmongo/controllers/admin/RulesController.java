package ru.pixelmongo.pixelmongo.controllers.admin;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.Rules;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.repositories.RulesRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/admin/rules")
public class RulesController {

    @Autowired
    private RulesRepository rules;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource msg;

    @Autowired
    private PopupMessageService popup;

    @Autowired
    private AdminLogService logs;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rules", rules.findAll());
        return "admin/rules";
    }

    @GetMapping("/new")
    public String newCategory(Model model, Locale loc) {
        Rules rule = new Rules();
        model.addAttribute("rule", rule);
        return "admin/rule";
    }

    @PostMapping("/new")
    public String createCategory(Model model,
            @ModelAttribute("rule") @Valid Rules rule,
            BindingResult binding,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {
        if(!binding.hasErrors()) {
            rule = rules.save(rule);
            logs.log("admin.log.rules.edit", null,
                    userService.getCurrentUser(), request.getRemoteAddr());
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.rules.created", null, loc),
                            PopupMessage.Type.OK),
                    request, response);
            return "redirect:/admin/rules/"+rule.getId();
        }
        return "admin/rule";
    }

    @GetMapping("/{id}")
    public String category(@PathVariable int id, Model model, Locale loc) {
        Rules rule = findRule(id, loc);
        model.addAttribute("rule", rule);
        return "admin/rule";
    }

    @PostMapping("/{id}")
    public String editCategory(@PathVariable int id,
            Model model,
            @ModelAttribute("rule") @Valid Rules rule,
            BindingResult binding,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {
        if(!binding.hasErrors()) {
            Rules savedRule = findRule(id, loc);
            rule.copyTo(savedRule);
            savedRule = rules.save(savedRule);
            logs.log("admin.log.rules.edit", null,
                    userService.getCurrentUser(), request.getRemoteAddr());
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.rules.edited", null, loc),
                            PopupMessage.Type.OK),
                    request, response);
            model.addAttribute("rule", savedRule);
        }
        return "admin/rule";
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable int id,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {
        Rules rule = findRule(id, loc);
        rules.delete(rule);
        logs.log("admin.log.rules.edit", null,
                userService.getCurrentUser(), request.getRemoteAddr());
        popup.sendUsingCookies(
                new PopupMessage(
                        msg.getMessage("admin.rules.deleted", null, loc),
                        PopupMessage.Type.WARN),
                request, response);
        return "redirect:/admin/rules";
    }

    private Rules findRule(int id, Locale loc) {
        return rules.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.rules", null, loc)));
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "rules");
    }

}
