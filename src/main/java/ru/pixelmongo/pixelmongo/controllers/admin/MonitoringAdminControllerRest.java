package ru.pixelmongo.pixelmongo.controllers.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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

import ru.pixelmongo.pixelmongo.model.dao.primary.MonitoringServer;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.MonitoringServerRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.MonitoringService;
import ru.pixelmongo.pixelmongo.services.MonitoringService.MonitoringResult;
import ru.pixelmongo.pixelmongo.services.OrdinaryUtilsService;
import ru.pixelmongo.pixelmongo.services.UserService;

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

    @Autowired
    private OrdinaryUtilsService ordinary;

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
            int changed = ordinary.reorder(this.servers, this.servers.findAllOrdinaries(), ids);
            if(changed > 0) {
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
        MonitoringService.LOGGER.catching(ex);
        return new ResultMessage(DefaultResult.ERROR, ex.toString());
    }

}
