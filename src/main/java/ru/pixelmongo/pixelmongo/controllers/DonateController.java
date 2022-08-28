package ru.pixelmongo.pixelmongo.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePage;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenSelectValue;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonatePageRepository;
import ru.pixelmongo.pixelmongo.services.UploadService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/donate")
public class DonateController {

    private final static String HIDDEN_SEE_PERM = "admin.panel.donate";

    @Autowired
    private DonatePageRepository pages;

    @Autowired
    private UserService userService;

    @Autowired
    private UploadService upload;

    @GetMapping
    public String pages(Model model) {
        List<DonatePage> pages = getVisiblePages();
        if(pages.size() == 1)
            return "redirect:/donate/"+pages.get(0).getTag();
        model.addAttribute("pages", pages);
        return "donate_pages";
    }

    @GetMapping("/{page}")
    public String page(@PathVariable("page") String pageTag, Model model) {
        DonatePage page = this.pages.findByTag(pageTag).filter(DonatePage::isEnabled)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("page", page);
        return "donate_page";
    }

    private List<DonatePage> getVisiblePages(){
        boolean hasPerm = userService.hasPerm(HIDDEN_SEE_PERM);
        List<DonatePage> pages = new ArrayList<DonatePage>();
        this.pages.findAllByOrderByOrdinaryAsc().forEach(p->{
            if(p.isEnabled() && (!p.isHidden() || hasPerm))
                pages.add(p);
        });
        return pages;
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "donate");
        model.addAttribute("images", new DonateImageResolver(upload));
        model.addAttribute("tokenHelper", new DonatePackTokenHelper());
    }

    public static class DonatePackTokenHelper {

        public int getCost(DonatePack pack, DonatePackTokenSelectValue selectToken, int index) {
            if(index >= 0 && index < selectToken.getValues().size()) {
                float costMult = 1f - Math.min(Math.max(pack.getDiscount() / 100f, 0), 1f);
                return (int)Math.round(selectToken.getCostValues().get(index)*costMult);
            }
            return 0;
        }

        public String getCostString(int cost) {
            return cost > 0 ? "+"+cost : ""+cost;
        }

        public String getCostString(DonatePack pack, DonatePackTokenSelectValue selectToken, int index) {
            return getCostString(getCost(pack, selectToken, index));
        }

    }

}
