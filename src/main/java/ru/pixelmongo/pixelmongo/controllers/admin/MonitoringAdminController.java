package ru.pixelmongo.pixelmongo.controllers.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.util.Pair;
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

import ru.pixelmongo.pixelmongo.model.dao.primary.MonitoringServer;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.model.dto.forms.MonitoringServerForm;
import ru.pixelmongo.pixelmongo.repositories.primary.MonitoringServerRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.MonitoringService;
import ru.pixelmongo.pixelmongo.services.MonitoringService.MonitoringResult;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/admin/monitoring")
public class MonitoringAdminController {

    @Autowired
    private MonitoringServerRepository servers;

    @Autowired
    private MonitoringService monitoring;

    @Autowired
    private MessageSource msg;

    @Autowired
    private PopupMessageService popup;

    @Autowired
    private AdminLogService logs;

    @Autowired
    private UserService userService;

    private volatile long lastUpdate;

    @GetMapping
    public String list(Model model) {
        List<MonitoringServerForm> servers = new ArrayList<>();
        this.servers.findAllByOrderByOrdinaryAsc().forEach(s->servers.add(makeForm(s)));
        model.addAttribute("servers", servers);
        return "admin/monitoring_servers";
    }

    @GetMapping("/new")
    public String newServer(Model model, Locale loc) {
        MonitoringServer server = new MonitoringServer();
        addServerAttributes(model, server, loc);
        model.addAttribute("method", "put");
        model.addAttribute("serverForm", makeForm(server));
        return "admin/monitoring_server";
    }

    @GetMapping("/{tag}")
    public String server(@PathVariable("tag") String tag, Model model, Locale loc) {
        MonitoringServer server = getServer(tag, loc);
        addServerAttributes(model, server, loc);
        model.addAttribute("method", "post");
        model.addAttribute("serverForm", makeForm(server));
        return "admin/monitoring_server";
    }

    @PutMapping("/new")
    public String newServerSave(
            @ModelAttribute("serverForm") @Valid MonitoringServerForm serverForm,
            BindingResult binding,
            Model model,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {
        MonitoringServer server = new MonitoringServer();
        checkTag(serverForm, server, binding, loc);
        if(!binding.hasErrors()) {
            serverForm.apply(server);
            int maxOrdinary = servers.getMaxOrdinary();
            server.setOrdinary(maxOrdinary+1);
            server = servers.save(server);
            this.lastUpdate = System.currentTimeMillis();
            monitoring.markServersChanged();
            logs.log("admin.log.monitoring.create",
                    new Object[] {server.getName()+" #"+server.getId()},
                    userService.getCurrentUser(), request.getRemoteAddr());
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.monitoring.created", null, loc),
                            PopupMessage.Type.OK),
                    request, response);
            return "redirect:/admin/monitoring/"+server.getTag();
        }
        model.addAttribute("method", "put");
        addServerAttributes(model, server, loc);
        return "admin/monitoring_server";
    }

    @PostMapping("/{tag}")
    public String serverSave(@PathVariable("tag") String tag,
            @ModelAttribute("serverForm") @Valid MonitoringServerForm serverForm,
            BindingResult binding,
            Model model,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {
        MonitoringServer server = getServer(tag, loc);
        checkTag(serverForm, server, binding, loc);
        if(!binding.hasErrors()) {
            serverForm.apply(server);
            server = servers.save(server);
            this.lastUpdate = System.currentTimeMillis();
            monitoring.markServersChanged();
            logs.log("admin.log.monitoring.edit",
                    new Object[] {server.getName()+" #"+server.getId()},
                    userService.getCurrentUser(), request.getRemoteAddr());
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.monitoring.edited", null, loc),
                            PopupMessage.Type.OK),
                    request, response);
        }
        model.addAttribute("method", "post");
        addServerAttributes(model, server, loc);
        return "admin/monitoring_server";
    }

    @DeleteMapping("/{tag}")
    public String serverDelete(@PathVariable("tag") String tag,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {
        MonitoringServer server = getServer(tag, loc);
        servers.delete(server);
        monitoring.markServersChanged();
        logs.log("admin.log.monitoring.delete",
                new Object[] {server.getName()+" #"+server.getId()},
                userService.getCurrentUser(), request.getRemoteAddr());
        popup.sendUsingCookies(
                new PopupMessage(
                        msg.getMessage("admin.monitoring.deleted", null, loc),
                        PopupMessage.Type.WARN),
                request, response);
        return "redirect:/admin/monitoring";
    }

    private void addServerAttributes(Model model, MonitoringServer server, Locale loc) {
        model.addAttribute("server", server);
        Pair<Integer, String> check = getCheckResult(server, loc);
        model.addAttribute("check_result", check.getSecond());
        model.addAttribute("check_status", check.getFirst());
    }

    private void checkTag(MonitoringServerForm form,
            MonitoringServer server,
            BindingResult binding,
            Locale loc) {
        if(form.getTag().equalsIgnoreCase("new")) {
            binding.addError(new FieldError("server", "tag",
                    msg.getMessage("value.denied", null, loc)));

        }else if(servers.findByTag(form.getTag())
                .filter(s->s.getId() != server.getId()).isPresent()) {
            binding.addError(new FieldError("server", "tag",
                    msg.getMessage("admin.monitoring.tag.busy", null, loc)));
        }
    }

    private MonitoringServerForm makeForm(MonitoringServer server) {
        boolean online = false;
        if(server.getId() != 0) {
            MonitoringResult r = monitoring.getMonitoringMap().get(server.getTag());
            online = r != null ? r.isOnline() : false;
        }
        return new MonitoringServerForm(server, online);
    }

    private Pair<Integer, String> getCheckResult(MonitoringServer server, Locale loc) {
        if(server.getId() == 0 || !server.isEnabled()
                || monitoring.getLastUpdateTime() < this.lastUpdate)
            return Pair.of(0, msg.getMessage("admin.monitoring.check.not", null, loc));
        MonitoringResult r = monitoring.getMonitoringMap().get(server.getTag());
        if(r == null)
            return Pair.of(0, msg.getMessage("admin.monitoring.check.not", null, loc));
        if(r.isOnline())
            return Pair.of(1, msg.getMessage("admin.monitoring.check.ok",
                    new Object[] {String.format("%1.3f", r.getPingTime()/1000.f)}, loc));
        return Pair.of(2, msg.getMessage("admin.monitoring.check.fail", null, loc));
    }

    private MonitoringServer getServer(String tag, Locale loc) {
        return servers.findByTag(tag).orElseThrow(()->
        new ResponseStatusException(HttpStatus.NOT_FOUND,
                msg.getMessage("error.status.404.monitoring_server", null, loc)));
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "monitoring");
    }

}
