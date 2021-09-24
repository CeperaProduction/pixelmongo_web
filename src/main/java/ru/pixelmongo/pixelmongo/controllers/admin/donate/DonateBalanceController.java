package ru.pixelmongo.pixelmongo.controllers.admin.donate;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/admin/donate/balance")
public class DonateBalanceController {

    @Autowired
    private UserRepository users;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource msg;

    @Autowired
    private AdminLogService logs;

    @Autowired
    private PopupMessageService popup;


    @GetMapping
    public String chooseUser() {
        return "admin/donate/give_money";
    }

    @GetMapping(params = "user")
    public String redirect(@RequestParam("user") String userName) {
        return "redirect:/admin/donate/balance/"+userName;
    }

    @GetMapping("/{user}")
    public String money(@PathVariable("user") String userName,
            Model model, Locale loc, HttpServletRequest request, HttpServletResponse response) {

        User user = users.findByName(userName).orElse(null);

        if(user == null) {
            popup("error.status.404.user", loc, PopupMessage.Type.ERROR, request, response);
            return "redirect:/admin/donate/balance";
        }

        model.addAttribute("user", user);

        return "admin/donate/give_money";
    }

    @PostMapping("/{user}")
    public String money(@PathVariable("user") String userName,
            @RequestParam("change") String change,
            @RequestParam("count") int count,
            Locale loc, HttpServletRequest request, HttpServletResponse response) {

        boolean give;
        switch(change) {
        case "give": give = true; break;
        case "take": give = false; break;
        default: throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                msg.getMessage("error.status.400", null, loc));
        }

        User user = users.findByName(userName).orElse(null);

        if(user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    msg.getMessage("error.status.404.user", null, loc));
        }

        int oldBalance = user.getBalance();
        int newBalance = Math.max(oldBalance + (give ? count : -count), 0);

        if(oldBalance != newBalance) {

            user.setBalance(newBalance);
            users.save(user);

            if(give) {
                log("admin.log.donate.balance.give", request, user.getName(), newBalance-oldBalance);
                popup("admin.donate.balance.given", loc, PopupMessage.Type.OK, request, response,
                        user.getName(), newBalance-oldBalance);
            }else {
                log("admin.log.donate.balance.take", request, user.getName(), oldBalance-newBalance);
                popup("admin.donate.balance.taken", loc, PopupMessage.Type.OK, request, response,
                        user.getName(), oldBalance-newBalance);
            }

        }

        return "redirect:/admin/donate/balance/"+user.getName();
    }

    //UTILS

    private void popup(String langKey, Locale loc, PopupMessage.Type type,
            HttpServletRequest request, HttpServletResponse response, Object... langValues) {
        popup.sendUsingCookies(
                new PopupMessage(msg.getMessage(langKey, langValues, loc), type),
                    request, response);
    }

    private void log(String langKey, HttpServletRequest request, Object... langValues) {
        logs.log(langKey, langValues, userService.getCurrentUser(), request.getRemoteAddr());
    }

    //OTHER

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "donate");
    }

}
