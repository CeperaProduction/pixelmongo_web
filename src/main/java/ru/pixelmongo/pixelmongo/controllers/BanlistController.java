package ru.pixelmongo.pixelmongo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.pixelmongo.pixelmongo.model.dao.sub.PlayerBanRecord;
import ru.pixelmongo.pixelmongo.repositories.sub.PlayerBanRecordRepository;
import ru.pixelmongo.pixelmongo.services.TemplateService;
import ru.pixelmongo.pixelmongo.services.UploadService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/banlist")
public class BanlistController {

    @Autowired
    private PlayerBanRecordRepository banlist;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private UploadService upload;

    @Autowired
    private UserService userService;

    @GetMapping
    public String banlist(@RequestParam(name="search", defaultValue = "") String search,
            @RequestParam(defaultValue = "1") int page, Model model) {
        if(search.length() < 3) search = "";
        Page<PlayerBanRecord> currentPage = banlist.getActiveBans(search, PageRequest.of(page-1, 50));
        model.addAttribute("bans_count", currentPage.getTotalElements());
        model.addAttribute("bans", currentPage.getContent());
        model.addAttribute("search", search);
        templateService.addPagination(model, page, currentPage.getTotalPages(), 9);
        model.addAttribute("proof", new BanlistControllerRest.BanlistProofHelper(userService, upload));
        return "banlist";
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "banlist");
    }



}
