package ru.pixelmongo.pixelmongo.controllers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateServer;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePackBuyForm;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonatePackRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateServerRepository;
import ru.pixelmongo.pixelmongo.services.DonateService;
import ru.pixelmongo.pixelmongo.services.UserService;

@RestController
@RequestMapping("/donate")
public class DonateControllerRest {

    @Autowired
    private DonatePackRepository packs;

    @Autowired
    private DonateServerRepository servers;

    @Autowired
    private MessageSource msg;

    @Autowired
    private UserService userService;

    @Autowired
    private DonateService donate;

    /*
    @GetMapping("/pack/{id}")
    public ResultMessage pack(@PathVariable("id") int packId, Locale loc) {
        DonatePack pack = getAvailablePack(packId, loc);

    }
    */

    @PostMapping("/buy/{pack}")
    public ResultMessage buyPack(@PathVariable("pack") int packId,
            DonatePackBuyForm packForm, Locale loc) {
        DonatePack pack = getAvailablePack(packId, loc);
        User user = userService.getCurrentUser();
        if(user.isAnonymous())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        user = userService.getStoredUser(user);

        DonateServer server = servers.findById(packId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.donate_server", null, loc)));
        int count = pack.isCountable() ? packForm.getCount() : 1;

        donate.buyPack(pack, user, server, packForm.getTokens(), count, false);

        int newBalance = user.getBalance();

        Map<String, Object> data = new HashMap<>();
        data.put("balance", newBalance);

        return new ResultDataMessage<Map<String, Object>>(DefaultResult.OK,
                msg.getMessage("donate.pack.bought",
                        new Object[] {pack.getTitle(), server.getDisplayName()}, loc),
                        data);
    }


    private DonatePack getAvailablePack(int packId, Locale loc) {
        return packs.findById(packId)
                .filter(p->p.isEnabled() && p.getCategory().isEnabled() && p.getCategory().getPage().isEnabled())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.donate_pack", null, loc)));
    }

}
