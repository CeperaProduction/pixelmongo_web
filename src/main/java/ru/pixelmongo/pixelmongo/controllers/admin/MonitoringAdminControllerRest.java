package ru.pixelmongo.pixelmongo.controllers.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.pixelmongo.pixelmongo.model.dao.MonitoringServer;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.MonitoringServerRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.MonitoringService;
import ru.pixelmongo.pixelmongo.services.UserService;
import ru.pixelmongo.pixelmongo.services.MonitoringService.MonitoringResult;

@RestController
@RequestMapping("/admin/monitoring/ajax")
public class MonitoringAdminControllerRest {

    @Autowired
    private MonitoringServerRepository servers;

    @Autowired
    private MonitoringService monitoring;

    @Autowired
    private MessageSource msg;

    @Autowired
    private AdminLogService logs;

    @Autowired
    private UserService userService;

    @PostMapping("/reorder")
    public ResultMessage reorder(@RequestParam("ids") String idsStr,
            HttpServletRequest request,
            HttpServletResponse response) {
        List<Integer> ids = null;
        try {
            ids = Arrays.asList(idsStr.split(",")).stream()
                    .map(Integer::parseInt).collect(Collectors.toList());
        }catch(Exception ex) {}
        if(ids != null && ids.size() > 1) {
            Map<Integer, MonitoringServer> servers = new HashMap<>();
            this.servers.findAllByOrderByOrdinaryAsc().forEach(s->servers.put(s.getId(), s));
            List<MonitoringServer> changed = new ArrayList<>();
            for(int i = 0; i < ids.size(); i++) {
                MonitoringServer server = servers.get(ids.get(i));
                if(server != null && server.getOrdinary() != i+1) {
                    server.setOrdinary(i+1);
                    changed.add(server);
                }
            }
            if(changed.size() > 0) {
                this.servers.saveAll(changed);
                monitoring.markServersChanged();
                logs.log("admin.log.monitoring.reorder", null,
                        userService.getCurrentUser(), request.getRemoteAddr());
            }
            return new ResultMessage(DefaultResult.OK, "Monitoring servers reordered");
        }
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResultMessage(DefaultResult.ERROR, "Wrong ids");
    }

    @PostMapping("/check")
    public ResultMessage check(@RequestParam("ip") String ip,
            @RequestParam("port") short port,
            Locale loc) {
        MonitoringResult r = monitoring.pingServer(new MonitoringServer("ping", "Test Ping", ip, port));
        if(r.isOnline())
            return new ResultMessage(DefaultResult.OK, msg.getMessage("admin.monitoring.check.ok",
                    new Object[] {String.format("%1.3f", r.getPingTime()/1000.f)}, loc));
        return new ResultMessage(DefaultResult.ERROR,
                msg.getMessage("admin.monitoring.check.fail", null, loc));
    }

    @ExceptionHandler
    public ResultMessage exceptionHandler(Exception ex) {
        return new ResultMessage(DefaultResult.ERROR, ex.toString());
    }

}
