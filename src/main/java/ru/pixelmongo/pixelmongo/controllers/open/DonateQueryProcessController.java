package ru.pixelmongo.pixelmongo.controllers.open;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateQuery;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateServer;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateQueryRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateServerRepository;

@RestController("/open/donate")
public class DonateQueryProcessController {

    @Autowired
    private DonateServerRepository servers;

    @Autowired
    private DonateQueryRepository queries;

    @PostMapping("/query")
    public Object process(@RequestParam("server") String serverConfigName,
            @RequestParam("key") String serverKey,
            @RequestParam("action") String action,
            @RequestParam(name = "id", required = false) Integer queryId) {

        DonateServer server = servers.findByConfigName(serverConfigName)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Server not found"));

        if(!server.getKey().isEmpty() && !server.getKey().equals(serverKey))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong server key");

        switch(action) {
        case "fetch":
            return fetch(server);
        case "complete":
            if(queryId == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query id is not providen");
            return complete(server, queryId);
        default:
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong action");
        }
    }

    private List<DonateQueryInfo> fetch(DonateServer server) {
        List<DonateQuery> queries = this.queries.getCurrentActiveQueries(server);
        return queries.stream().map(DonateQueryInfo::new).collect(Collectors.toList());
    }

    private String complete(DonateServer server, int queryId) {
        DonateQuery query = this.queries.findById(queryId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Query not found"));
        if(!query.getServer().equals(server.getConfigName()))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "This query is owned by another server");
        if(query.isDone())
            throw new ResponseStatusException(HttpStatus.GONE, "This query is already done");
        query.setDone(true);
        query.setDateCompleted((int) (System.currentTimeMillis()/1000));
        this.queries.save(query);
        return "success";
    }

    /**
     * {@link DonateQuery} representation for MegaQuery mod/plugin
     */
    public static class DonateQueryInfo{

        private int id, invSpace;
        private String title, player, cmds;
        //isHiden is a true property name for MegaQuery
        private boolean isHiden, offline;

        public DonateQueryInfo(DonateQuery query) {
            this.id = query.getId();
            this.title = query.getTitle();
            this.player = query.getPlayer();
            this.cmds = String.join(";",
                    query.getCommands().stream().map(s->s.replace(';', ','))
                        .collect(Collectors.toList()));
            this.invSpace = query.getInvSpace();
        }

        public int getId() {
            return id;
        }

        public int getInvSpace() {
            return invSpace;
        }

        public String getTitle() {
            return title;
        }

        public String getPlayer() {
            return player;
        }

        public String getCmds() {
            return cmds;
        }

        public boolean isHiden() {
            return isHiden;
        }

        public boolean isOffline() {
            return offline;
        }

    }



}
