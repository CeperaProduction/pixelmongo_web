package ru.pixelmongo.pixelmongo.controllers.admin;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.primary.Promocode;
import ru.pixelmongo.pixelmongo.model.dao.primary.PromocodeActivation;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.model.dto.forms.PromocodeForm;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.PromocodeService;
import ru.pixelmongo.pixelmongo.services.TemplateService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/admin/promocodes")
public class PromocodeAdminController {

    @Autowired
    private PromocodeService promocodes;

    @Autowired
    private UserService users;

    @Autowired
    private MessageSource msg;

    @Autowired
    private PopupMessageService popup;

    @Autowired
    private AdminLogService logs;

    @Autowired
    private TemplateService tpl;

    @GetMapping
    public String list(@RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        Pageable p = PageRequest.of(page-1, 100);
        Page<Promocode> result = promocodes.get(p);
        model.addAttribute("promocodes", result.getContent());
        model.addAttribute("promocodes_count", result.getTotalElements());
        tpl.addPagination(model, page, result.getTotalPages(), 9);
        return "admin/promocodes";
    }

    @GetMapping("/new")
    public String newPromocode(Model model) {
        model.addAttribute("method", "put");
        model.addAttribute("promocode", new Promocode());
        model.addAttribute("promocodeForm", new PromocodeForm());
        model.addAttribute("activations", Collections.emptyList());
        model.addAttribute("activations_count", 0);
        tpl.addPagination(model, 1, 1, 9);
        return "admin/promocode_edit";
    }

    @PutMapping("/new")
    public String newPromocode(@ModelAttribute(name = "promocodeForm") @Valid PromocodeForm form,
            BindingResult binding, Model model, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {

        if(!binding.hasErrors()) {

            User user = users.getCurrentUser();

            Promocode promocode = new Promocode(form.getCode(), form.getTitle());
            promocode.setValue(form.getValue());
            promocode.setMaxUses(form.getMaxUsages());
            Date endDate = form.getEndTime() == null || form.getEndTime() <= 0
                    ? null : new Date(1000L*form.getEndTime());
            promocode.setEndDate(endDate);

            promocode = promocodes.save(promocode);

            logs.log("admin.log.promocodes.create", new Object[] {
                    promocode.getTitle()+" #"+promocode.getId()}, user, request.getRemoteAddr());
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.promocodes.created", null, loc),
                            PopupMessage.Type.OK),
                    request, response);

            return "redirect:/admin/promocodes/"+promocode.getId();

        }

        model.addAttribute("method", "put");
        model.addAttribute("promocode", new Promocode());
        model.addAttribute("activations", Collections.emptyList());
        model.addAttribute("activations_count", 0);
        tpl.addPagination(model, 1, 1, 9);
        return "admin/promocode_edit";
    }

    @GetMapping("/{id}")
    public String editPromocode(@PathVariable(name = "id") int id,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model, Locale loc) {
        Promocode promocode = findPromocode(id, loc);

        model.addAttribute("method", "post");

        model.addAttribute("promocode", promocode);
        model.addAttribute("promocodeForm", new PromocodeForm(promocode));

        Page<PromocodeActivation> activationsPage = promocodes
                .getActivations(promocode, PageRequest.of(page-1, 100));
        model.addAttribute("activations", activationsPage.getContent());
        model.addAttribute("activations_count", activationsPage.getTotalElements());
        tpl.addPagination(model, page, activationsPage.getTotalPages(), 9);

        return "admin/promocode_edit";
    }

    @PostMapping("/{id}")
    public String editPromocode(@PathVariable(name = "id") int id,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @ModelAttribute(name = "promocodeForm") @Valid PromocodeForm form,
            BindingResult binding, Model model, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {

        Promocode promocode = findPromocode(id, loc);

        if(!binding.hasErrors()) {

            User user = users.getCurrentUser();

            promocode.setTitle(form.getTitle());
            promocode.setValue(form.getValue());
            promocode.setMaxUses(form.getMaxUsages());
            Date endDate = form.getEndTime() == null || form.getEndTime() <= 0
                    ? null : new Date(1000L*form.getEndTime());
            promocode.setEndDate(endDate);

            promocode = promocodes.save(promocode);

            logs.log("admin.log.promocodes.edit", new Object[] {
                    promocode.getTitle()+" #"+promocode.getId()}, user, request.getRemoteAddr());
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.promocodes.edited", null, loc),
                            PopupMessage.Type.OK),
                    request, response);

            return "redirect:/admin/promocodes/"+promocode.getId();

        }

        model.addAttribute("method", "post");

        model.addAttribute("promocode", promocode);

        Page<PromocodeActivation> activationsPage = promocodes
                .getActivations(promocode, PageRequest.of(page-1, 100));
        model.addAttribute("activations", activationsPage.getContent());
        model.addAttribute("activations_count", activationsPage.getTotalElements());
        tpl.addPagination(model, page, activationsPage.getTotalPages(), 9);

        return "admin/promocode_edit";
    }

    @DeleteMapping("/{id}")
    public String deletePromocode(@PathVariable(name = "id") int id,
            Model model, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {

        Promocode promocode = findPromocode(id, loc);

        User user = users.getCurrentUser();

        promocodes.remove(promocode);

        logs.log("admin.log.promocodes.delete", new Object[] {
                promocode.getTitle()+" #"+promocode.getId()}, user, request.getRemoteAddr());
        popup.sendUsingCookies(
                new PopupMessage(
                        msg.getMessage("admin.promocodes.deleted", null, loc),
                        PopupMessage.Type.WARN),
                request, response);

        return "redirect:/admin/promocodes";
    }

    private Promocode findPromocode(int id, Locale loc) {
        return promocodes.get(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.promocode", null, loc)));
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "promocodes");
    }

}
