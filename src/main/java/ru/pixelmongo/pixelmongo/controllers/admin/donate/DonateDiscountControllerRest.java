package ru.pixelmongo.pixelmongo.controllers.admin.donate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateCategory;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePage;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateCategoryRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonatePackRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonatePageRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.UserService;

@RestController
@RequestMapping("/admin/donate/discount")
public class DonateDiscountControllerRest {

    @Autowired
    private DonatePageRepository pages;

    @Autowired
    private DonateCategoryRepository categories;

    @Autowired
    private DonatePackRepository packs;

    @Autowired
    private AdminLogService logs;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource msg;

    @GetMapping("/categories")
    public ResultMessage getCategories(@RequestParam("page") int pageId) {
        DonatePage page = find(pages.findById(pageId));
        List<IdTitle> its = new ArrayList<>();
        page.getCategories().forEach(c->its.add(new IdTitle(c.getId(), c.getTitle())));
        return new ResultDataMessage<List<IdTitle>>(DefaultResult.OK, "List of categories", its);
    }

    @GetMapping("/packs")
    public ResultMessage getPacks(@RequestParam("category") int categoryId) {
        DonateCategory category = find(categories.findById(categoryId));
        List<IdTitle> its = new ArrayList<>();
        category.getPacks().forEach(p->its.add(new IdTitle(p.getId(), p.getTitle())));
        return new ResultDataMessage<List<IdTitle>>(DefaultResult.OK, "List of packs", its);
    }

    @PostMapping
    public ResultMessage discount(@RequestParam(name = "page", defaultValue = "0") int pageId,
            @RequestParam(name = "category", defaultValue = "0") int categoryId,
            @RequestParam(name = "pack", defaultValue = "0") int packId,
            @RequestParam(name = "value") int valueInt,
            HttpServletRequest request,
            Locale loc) {

        byte value = (byte) Math.max(Math.min(valueInt, 100), 0);

        String message;

        if(packId != 0) {
            DonatePack pack = find(packs.findById(packId));
            pack.setDiscount(value);
            packs.save(pack);
            log("admin.log.donate.discount.set.pack", request, value, pack.getTitle()+" "+pack.getId());
            message = msg.getMessage("admin.donate.discount.set.pack",
                    new Object[] {value, pack.getTitle()+" #"+pack.getId()}, loc);
        }else if(categoryId != 0) {
            DonateCategory category = find(categories.findById(categoryId));
            packs.setDiscount(category, value);
            log("admin.log.donate.discount.set.category", request, value, category.getTitle()+" "+category.getId());
            message = msg.getMessage("admin.donate.discount.set.category",
                    new Object[] {value, category.getTitle()+" #"+category.getId()}, loc);
        }else if(pageId != 0) {
            DonatePage page = find(pages.findById(pageId));
            packs.setDiscount(page, value);
            log("admin.log.donate.discount.set.page", request, value, page.getTitle()+" "+page.getId());
            message = msg.getMessage("admin.donate.discount.set.page",
                    new Object[] {value, page.getTitle()+" #"+page.getId()}, loc);
        }else {
            packs.setDiscount(value);
            log("admin.log.donate.discount.set", request, value);
            message = msg.getMessage("admin.donate.discount.set", new Object[] {value}, loc);
        }

        return new ResultMessage(DefaultResult.OK, message);
    }

    private <T> T find(Optional<T> searchResult) {
        return searchResult.orElseThrow(()->notFound());
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Target not found");
    }

    private void log(String langKey, HttpServletRequest request, Object... langValues) {
        logs.log(langKey, langValues, userService.getCurrentUser(), request.getRemoteAddr());
    }

    public static class IdTitle {

        private int id;
        private String title;

        public IdTitle() {}

        public IdTitle(int id, String title) {
            this.id = id;
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String name) {
            this.title = name;
        }

    }

}
