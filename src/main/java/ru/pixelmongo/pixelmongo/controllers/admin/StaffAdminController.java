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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.primary.Staff;
import ru.pixelmongo.pixelmongo.model.dao.primary.StaffDisplay;
import ru.pixelmongo.pixelmongo.model.dao.primary.StaffDisplayForeground;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.model.dto.forms.StaffManageForm;
import ru.pixelmongo.pixelmongo.repositories.primary.StaffRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/admin/staff")
public class StaffAdminController {

    @Autowired
    private StaffRepository staffs;

    @Autowired
    private MessageSource msg;

    @Autowired
    private PopupMessageService popup;

    @Autowired
    private AdminLogService logs;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository users;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("staffs", staffs.findAllByOrderByOrdinaryAsc());
        return "admin/staffs";
    }

    @GetMapping("/new")
    public String newStaff(Model model) {
        Staff staff = new Staff(new User(), null);
        applyStaff(model, "put", staff);
        model.addAttribute("staffForm", new StaffManageForm());
        return "admin/staff";
    }

    @PutMapping("/new")
    public String newStaff(@ModelAttribute("staffForm") @Valid StaffManageForm form,
            BindingResult binding, Model model,
            Locale loc, HttpServletRequest request, HttpServletResponse response) {

        User user = users.findByName(form.getUser()).orElse(null);

        if(user == null) {
            binding.addError(new FieldError("staffForm", "user",
                    msg.getMessage("error.status.404.user", null, loc)));
        }

        if(staffs.findByUser(user).isPresent()) {
            binding.addError(new FieldError("staffForm", "user",
                    msg.getMessage("admin.staff.already", null, loc)));
        }

        if(!binding.hasErrors()) {

            Staff staff = new Staff(user, form.getTitle());
            staff.setDisplay(form.getDisplay());
            staff.setOrdinary(staffs.getMaxOrdinary()+1);

            logs.log("admin.log.staff.add", new Object[] {user.getName()},
                    userService.getCurrentUser(), request.getRemoteAddr());
            popup.sendUsingCookies( new PopupMessage(
                            msg.getMessage("admin.staff.edited", null, loc),
                            PopupMessage.Type.OK),
                    request, response);

            staffs.save(staff);

            return "redirect:/admin/staff";

        }

        applyStaff(model, "put", new Staff(new User(), null));
        return "admin/staff";
    }

    @GetMapping("/{user}")
    public String editStaff(@PathVariable("user") String userName, Model model, Locale loc) {

        User user = users.findByName(userName)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.user", null, loc)));
        Staff staff = staffs.findByUser(user)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.staff", null, loc)));

        applyStaff(model, "post", staff);
        model.addAttribute("staffForm", new StaffManageForm(staff));
        return "admin/staff";
    }

    @PostMapping("/{user}")
    public String editStaff(@PathVariable("user") String userName,
            @ModelAttribute("staffForm") @Valid StaffManageForm form,
            BindingResult binding, Model model,
            Locale loc, HttpServletRequest request, HttpServletResponse response) {

        User user = users.findByName(userName)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.user", null, loc)));
        Staff staff = staffs.findByUser(user)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.staff", null, loc)));

        if(!binding.hasErrors()) {

            staff.setTitle(form.getTitle());
            staff.setDisplay(form.getDisplay());

            logs.log("admin.log.staff.edit", new Object[] {user.getName()},
                    userService.getCurrentUser(), request.getRemoteAddr());
            popup.sendUsingCookies(new PopupMessage(
                            msg.getMessage("admin.staff.edited", null, loc),
                            PopupMessage.Type.OK),
                    request, response);

            staffs.save(staff);

            return "redirect:/admin/staff";

        }

        applyStaff(model, "post", staff);
        model.addAttribute("staffForm", new StaffManageForm(staff));
        return "admin/staff";
    }

    @DeleteMapping("/{user}")
    public String deleteStaff(@PathVariable("user") String userName,
            Locale loc, HttpServletRequest request, HttpServletResponse response) {

        User user = users.findByName(userName)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.user", null, loc)));
        Staff staff = staffs.findByUser(user)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.staff", null, loc)));

        staffs.delete(staff);

        logs.log("admin.log.staff.delete", new Object[] {user.getName()},
                userService.getCurrentUser(), request.getRemoteAddr());
        popup.sendUsingCookies(new PopupMessage(
                        msg.getMessage("admin.staff.edited", null, loc),
                        PopupMessage.Type.INFO),
                request, response);

        return "redirect:/admin/staff";
    }


    private void applyStaff(Model model, String method, Staff staff) {
        model.addAttribute("method", method);
        model.addAttribute("staff", staff);
        model.addAttribute("displayTypes", StaffDisplay.getAllVariants());
        model.addAttribute("displayHelper", new StaffDisplayHelper());
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "users");
    }

    public static class StaffDisplayHelper{

        private StaffDisplayForeground lastFg;

        public boolean fgChanged(StaffDisplay display) {
            boolean res = lastFg != null && lastFg != display.getForeground();
            lastFg = display.getForeground();
            return res;
        }

    }

}
