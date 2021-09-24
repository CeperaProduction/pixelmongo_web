package ru.pixelmongo.pixelmongo.controllers.admin.donate;

import java.util.Locale;
import java.util.Optional;

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

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateServer;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonateServerForm;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateServerRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.UserService;
import ru.pixelmongo.pixelmongo.utils.RandomUtils;

@Controller
@RequestMapping("/admin/donate/servers")
public class DonateServersController {

    @Autowired
    private DonateServerRepository servers;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource msg;

    @Autowired
    private PopupMessageService popup;

    @Autowired
    private AdminLogService logs;

    @GetMapping
    public String servers(Model model) {

        model.addAttribute("servers", servers.findAll());

        return "admin/donate/servers";
    }

    @GetMapping("/new")
    public String serverNew(Model model, Locale loc) {

        model.addAttribute("method", "put");
        model.addAttribute("server", new DonateServer());
        DonateServerForm form = new DonateServerForm();
        form.setKey(RandomUtils.generateRandomKey());
        model.addAttribute("serverForm", form);

        return "admin/donate/server_form";
    }

    @PutMapping("/new")
    public String serverNew(@ModelAttribute("serverForm") @Valid DonateServerForm serverForm,
            BindingResult binding,
            Model model,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {

        if(servers.findByConfigName(serverForm.getConfigName()).isPresent()) {
            binding.addError(new FieldError("serverForm", "configName",
                    msg.getMessage("admin.donate.server.tag.busy", null, loc)));
        }

        DonateServer server = new DonateServer();

        if(!binding.hasErrors()) {

            serverForm.apply(server);

            server = servers.save(server);

            log("admin.log.donate.server.create", request, server.getDisplayName()+" #"+server.getId());
            popup("admin.donate.server.created", loc, PopupMessage.Type.OK, request, response);

            return "redirect:/admin/donate/servers/"+server.getId();
        }

        model.addAttribute("method", "put");
        model.addAttribute("server", server);

        return "admin/donate/server_form";
    }

    @GetMapping("/{server}")
    public String serverEdit(@PathVariable("server") int serverId, Model model, Locale loc) {

        DonateServer server = find(servers.findById(serverId), loc);

        model.addAttribute("method", "post");
        model.addAttribute("server", server);
        model.addAttribute("serverForm", new DonateServerForm(server));

        return "admin/donate/server_form";
    }

    @PostMapping("/{server}")
    public String serverEdit(@PathVariable("server") int serverId,
            @ModelAttribute("serverForm") @Valid DonateServerForm serverForm,
            BindingResult binding,
            Model model,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {

        DonateServer server = find(servers.findById(serverId), loc);

        if(servers.findByConfigName(serverForm.getConfigName())
                .filter(s->s.getId() != serverId).isPresent()) {
            binding.addError(new FieldError("serverForm", "configName",
                    msg.getMessage("admin.donate.server.tag.busy", null, loc)));
        }

        if(!binding.hasErrors()) {

            serverForm.apply(server);

            server = servers.save(server);

            log("admin.log.donate.server.edit", request, server.getDisplayName()+" #"+server.getId());
            popup("admin.donate.server.edited", loc, PopupMessage.Type.OK, request, response);

        }

        model.addAttribute("method", "post");
        model.addAttribute("server", server);

        return "admin/donate/server_form";
    }

    @DeleteMapping("/{server}")
    public String deleteServer(@PathVariable("server") int serverId,
            Locale loc, HttpServletRequest request, HttpServletResponse response) {

        DonateServer server = find(servers.findById(serverId), loc);

        servers.delete(server);

        log("admin.log.donate.server.delete", request, server.getDisplayName()+" #"+server.getId());
        popup("admin.donate.server.deleted", loc, PopupMessage.Type.WARN, request, response);

        return "redirect:/admin/donate/servers";
    }

    private <T> T find(Optional<T> searchResult, Locale loc) {
        return searchResult.orElseThrow(()->notFound(loc));
    }

    private ResponseStatusException notFound(Locale loc) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                msg.getMessage("error.status.404", null, loc));
    }

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
