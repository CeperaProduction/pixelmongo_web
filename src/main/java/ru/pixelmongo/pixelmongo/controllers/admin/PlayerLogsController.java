package ru.pixelmongo.pixelmongo.controllers.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.pixelmongo.pixelmongo.model.dao.sub.PlayerLogRecord;
import ru.pixelmongo.pixelmongo.repositories.sub.PlayerLogRecordRepository;
import ru.pixelmongo.pixelmongo.services.TemplateService;

@Controller
@RequestMapping("/admin/playerlogs")
public class PlayerLogsController {

    private static final int SERVER_CACHE_UPDATE_PERIOD = 600000;

    @Autowired
    private PlayerLogRecordRepository logs;

    @Autowired
    private TemplateService tpl;

    private List<String> serverCache = Collections.emptyList();
    private volatile long lastServerCacheUpdate;

    @GetMapping
    public String chooseServer(Model model) {

        model.addAttribute("servers", getServers());

        return "admin/playerlogs/choose_server";
    }

    @GetMapping(params = {"server"})
    public String choosePlayer(Model model, @RequestParam("server") String server) {

        model.addAttribute("server", server);
        model.addAttribute("players", logs.findPlayers(server));

        return "admin/playerlogs/player";
    }

    @GetMapping(params = {"server", "player"})
    public String playerLogs(Model model,
            @RequestParam("server") String server,
            @RequestParam("player") String player,
            @RequestParam("start") int start,
            @RequestParam("end") int end,
            @RequestParam(name = "page", defaultValue = "1") int page) {

        List<String> players = logs.findPlayers(server);

        Pageable p = PageRequest.of(page-1, 1000);
        Page<PlayerLogRecord> pageRecords = logs.findLogs(server, player, start, end, p);

        model.addAttribute("server", server);
        model.addAttribute("player", player);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("players", players);
        model.addAttribute("logs", pageRecords.getContent());
        tpl.addPagination(model, page, pageRecords.getTotalPages(), 9);

        return "admin/playerlogs/player";
    }

    @GetMapping(params = "search=")
    public String searchLogs(Model model) {

        model.addAttribute("logs", Collections.<String>emptyList());
        model.addAttribute("serverContext", new PlayerLogServerContext());

        return "admin/playerlogs/search";
    }

    @GetMapping(params = "search")
    public String searchLogs(Model model,
            @RequestParam("search") String search,
            @RequestParam("start") int start,
            @RequestParam("end") int end,
            @RequestParam(name = "page", defaultValue = "1") int page) {

        if(!StringUtils.hasText(search) && search.trim().length() < 3) {
            return searchLogs(model);
        }

        search = search.trim();
        Pageable pageable = PageRequest.of(page-1, 1000);
        Page<PlayerLogRecord> pageRecords = logs.findLogs(search, start, end, pageable);

        model.addAttribute("search", search);
        model.addAttribute("logs", pageRecords.getContent());
        model.addAttribute("serverContext", new PlayerLogServerContext());
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        tpl.addPagination(model, page, pageRecords.getTotalPages(), 9);

        return "admin/playerlogs/search";
    }

    private List<String> getServers(){
        long now = System.currentTimeMillis();
        if(now - SERVER_CACHE_UPDATE_PERIOD > lastServerCacheUpdate) {
            lastServerCacheUpdate = now;
            serverCache = Collections.unmodifiableList(new ArrayList<>(logs.findServers()));
        }
        return serverCache;
    }

    public static class PlayerLogServerContext {

        private String currentServer;

        public boolean serverChanged(PlayerLogRecord nextRecord) {
            if(!nextRecord.getServer().equals(currentServer)) {
                currentServer = nextRecord.getServer();
                return true;
            }
            return false;
        }

    }

    //OTHER

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "player_logs");
    }

}
