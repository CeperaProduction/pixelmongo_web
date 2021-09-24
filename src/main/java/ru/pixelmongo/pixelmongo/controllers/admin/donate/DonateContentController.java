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

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateCategory;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePage;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonateCategoryForm;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePackForm;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePageForm;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateCategoryRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonatePackRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonatePageRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateServerRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.DonateService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/admin/donate/pages")
public class DonateContentController {

    @Autowired
    private DonatePageRepository pages;

    @Autowired
    private DonateCategoryRepository categories;

    @Autowired
    private DonatePackRepository packs;

    @Autowired
    private DonateServerRepository servers;

    @Autowired
    private DonateService donateService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource msg;

    @Autowired
    private AdminLogService logs;

    @Autowired
    private PopupMessageService popup;

    //ALL PAGES

    @GetMapping
    public String pages(Model model) {

        model.addAttribute("pages", pages.findAllByOrderByOrdinaryAsc());

        return "admin/donate/pages";
    }

    //PAGE CONTENT

    @GetMapping("/{page}")
    public String pageContent(@PathVariable("page") String pageTag, Model model, Locale loc) {

        DonatePage page = find(pages.findByTag(pageTag), loc);

        model.addAttribute("page", page);

        return "admin/donate/page";
    }

    //PAGE EDITOR

    @GetMapping("/new")
    public String newPage(Model model) {

        model.addAttribute("method", "put");
        model.addAttribute("page", new DonatePage());
        model.addAttribute("pageForm", new DonatePageForm());
        model.addAttribute("servers", servers.findAll());

        return "admin/donate/page_form";
    }

    @PutMapping("/new")
    public String newPage(@ModelAttribute("pageForm") @Valid DonatePageForm pageForm,
            BindingResult binding,
            Model model,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {

        if("new".equals(pageForm.getTag()) || "reorder".equals(pageForm.getTag())) {
            binding.addError(new FieldError("pageForm", "tag",
                    msg.getMessage("value.denied", null, loc)));
        }else if(pages.findByTag(pageForm.getTag()).isPresent()) {
            binding.addError(new FieldError("pageForm", "tag",
                    msg.getMessage("admin.donate.page.tag.busy", null, loc)));
        }

        DonatePage page = new DonatePage();

        if(!binding.hasErrors()) {

            pageForm.apply(page, servers);

            page.setOrdinary(pages.getMaxOrdinary()+1);

            page = pages.save(page);

            log("admin.log.donate.page.create", request, page.getTitle()+" #"+page.getId());
            popup("admin.donate.page.created", loc, PopupMessage.Type.OK, request, response);

            return "redirect:/admin/donate/pages/"+page.getTag();
        }

        model.addAttribute("method", "put");
        model.addAttribute("page", page);
        model.addAttribute("servers", servers.findAll());

        return "admin/donate/page_form";
    }

    @GetMapping("/{page}/edit")
    public String editPage(@PathVariable("page") String pageTag, Model model, Locale loc) {

        DonatePage page = find(pages.findByTag(pageTag), loc);

        model.addAttribute("method", "post");
        model.addAttribute("page", page);
        model.addAttribute("pageForm", new DonatePageForm(page));
        model.addAttribute("servers", servers.findAll());

        return "admin/donate/page_form";
    }

    @PostMapping("/{page}/edit")
    public String editPage(@PathVariable("page") String pageTag,
            @ModelAttribute("pageForm") @Valid DonatePageForm pageForm,
            BindingResult binding,
            Model model,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {

        DonatePage page = find(pages.findByTag(pageTag), loc);

        final int pageId = page.getId();
        if("new".equals(pageForm.getTag()) || "reorder".equals(pageForm.getTag())) {
            binding.addError(new FieldError("pageForm", "tag",
                    msg.getMessage("value.denied", null, loc)));
        }else if(pages.findByTag(pageForm.getTag()).filter(p->p.getId() != pageId).isPresent()) {
            binding.addError(new FieldError("pageForm", "tag",
                    msg.getMessage("admin.donate.page.tag.busy", null, loc)));
        }

        if(!binding.hasErrors()) {

            pageForm.apply(page, servers);

            page = pages.save(page);

            log("admin.log.donate.page.edit", request, page.getTitle()+" #"+page.getId());
            popup("admin.donate.page.edited", loc, PopupMessage.Type.OK, request, response);
        }

        model.addAttribute("method", "post");
        model.addAttribute("page", page);
        model.addAttribute("servers", servers.findAll());

        return "admin/donate/page_form";
    }

    @DeleteMapping("/{page}/edit")
    public String deletePage(@PathVariable("page") String pageTag,
            Locale loc, HttpServletRequest request, HttpServletResponse response) {

        DonatePage page = find(pages.findByTag(pageTag), loc);

        pages.delete(page);

        log("admin.log.donate.page.delete", request, page.getTitle()+" #"+page.getId());
        popup("admin.donate.page.deleted", loc, PopupMessage.Type.WARN, request, response);

        return "redirect:/admin/donate/pages";
    }

    //CATEGORY EDITOR

    @GetMapping("/{page}/category/new")
    public String categoryNew(@PathVariable("page") String pageTag, Model model, Locale loc) {

        DonatePage page = find(pages.findByTag(pageTag), loc);

        model.addAttribute("method", "put");
        model.addAttribute("page", page);
        model.addAttribute("pages", pages.findAllByOrderByOrdinaryAsc());
        model.addAttribute("category", new DonateCategory("", page));
        model.addAttribute("categoryForm", new DonateCategoryForm(page));

        return "admin/donate/category_form";
    }

    @PutMapping("/{page}/category/new")
    public String categoryNew(@PathVariable("page") String pageTag,
            @ModelAttribute("categoryForm") @Valid DonateCategoryForm categoryForm,
            BindingResult binding,
            Model model,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {

        DonatePage page = find(pages.findByTag(pageTag), loc);
        DonateCategory category = new DonateCategory("", page);

        if(!binding.hasErrors()) {

            categoryForm.apply(category, pages);

            category.setOrdinary(categories.getMaxOrdinary(category.getPage())+1);

            category = categories.save(category);

            log("admin.log.donate.category.create", request, category.getTitle()+" #"+category.getId());
            popup("admin.donate.category.created", loc, PopupMessage.Type.OK, request, response);

            return "redirect:/admin/donate/pages/"+category.getPage().getTag();
        }

        model.addAttribute("method", "put");
        model.addAttribute("page", page);
        model.addAttribute("pages", pages.findAllByOrderByOrdinaryAsc());
        model.addAttribute("category", category);

        return "admin/donate/category_form";
    }

    @GetMapping("/{page}/category/{category}")
    public String categoryEdit(@PathVariable("page") String pageTag,
            @PathVariable("category") int categoryId, Model model, Locale loc) {

        DonateCategory category = find(categories.findById(categoryId), loc);
        DonatePage page = category.getPage();

        if(!page.getTag().equals(pageTag))
            throw notFound(loc);

        model.addAttribute("method", "post");
        model.addAttribute("page", page);
        model.addAttribute("pages", pages.findAllByOrderByOrdinaryAsc());
        model.addAttribute("category", category);
        model.addAttribute("categoryForm", new DonateCategoryForm(category));

        return "admin/donate/category_form";
    }

    @PostMapping("/{page}/category/{category}")
    public String categoryEdit(@PathVariable("page") String pageTag,
            @PathVariable("category") int categoryId,
            @ModelAttribute("categoryForm") @Valid DonateCategoryForm categoryForm,
            BindingResult binding,
            Model model,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {

        DonateCategory category = find(categories.findById(categoryId), loc);
        DonatePage page = category.getPage();

        if(!page.getTag().equals(pageTag))
            throw notFound(loc);

        if(!binding.hasErrors()) {

            categoryForm.apply(category, pages);

            category = categories.save(category);

            log("admin.log.donate.category.edit", request, category.getTitle()+" #"+category.getId());
            popup("admin.donate.category.edited", loc, PopupMessage.Type.OK, request, response);

            if(category.getPage().getId() != page.getId())
                return "redirect:/admin/donate/pages/"+category.getPage().getTag()+"/category/"+category.getId();
        }

        model.addAttribute("method", "post");
        model.addAttribute("page", page);
        model.addAttribute("pages", pages.findAllByOrderByOrdinaryAsc());
        model.addAttribute("category", category);

        return "admin/donate/category_form";
    }

    @DeleteMapping("/{page}/category/{category}")
    public String deleteCategory(@PathVariable("page") String pageTag,
            @PathVariable("category") int categoryId,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {

        DonateCategory category = find(categories.findById(categoryId), loc);
        DonatePage page = category.getPage();

        if(!page.getTag().equals(pageTag))
            throw notFound(loc);

        categories.delete(category);

        log("admin.log.donate.category.delete", request, category.getTitle()+" #"+category.getId());
        popup("admin.donate.category.deleted", loc, PopupMessage.Type.WARN, request, response);

        return "redirect:/admin/donate/pages/"+page.getTag();
    }

    //PACK EDITOR

    @GetMapping("/{page}/pack/new")
    public String packNew(@PathVariable("page") String pageTag, Model model, Locale loc) {

        DonatePage page = find(pages.findByTag(pageTag), loc);

        model.addAttribute("method", "put");
        model.addAttribute("page", page);
        model.addAttribute("pack", new DonatePack());
        model.addAttribute("packForm", new DonatePackForm());
        model.addAttribute("token_types", DonatePackTokenType.values());

        return "admin/donate/pack_form";
    }

    @PutMapping("/{page}/pack/new")
    public String packNew(@PathVariable("page") String pageTag,
            @ModelAttribute("packForm") @Valid DonatePackForm packForm,
            BindingResult binding,
            Model model,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {

        DonatePage page = find(pages.findByTag(pageTag), loc);
        DonatePack pack = new DonatePack();

        if(!binding.hasErrors()) {

            packForm.apply(pack, donateService, categories, servers);

            pack.setOrdinary(packs.getMaxOrdinary(pack.getCategory())+1);

            pack = packs.save(pack);

            log("admin.log.donate.pack.create", request, pack.getTitle()+" #"+pack.getId());
            popup("admin.donate.pack.created", loc, PopupMessage.Type.OK, request, response);

            return "redirect:/admin/donate/pages/"+page.getTag();
        }

        model.addAttribute("method", "put");
        model.addAttribute("page", page);
        model.addAttribute("pack", pack);
        model.addAttribute("token_types", DonatePackTokenType.values());

        return "admin/donate/pack_form";
    }

    @GetMapping("/{page}/pack/{pack}")
    public String packEdit(@PathVariable("page") String pageTag,
            @PathVariable("pack") int packId, Model model, Locale loc) {

        DonatePack pack = find(packs.findById(packId), loc);
        DonatePage page = pack.getCategory().getPage();

        if(!page.getTag().equals(pageTag))
            throw notFound(loc);

        model.addAttribute("method", "post");
        model.addAttribute("page", page);
        model.addAttribute("pack", pack);
        model.addAttribute("packForm", new DonatePackForm(pack, donateService));
        model.addAttribute("token_types", DonatePackTokenType.values());

        return "admin/donate/pack_form";
    }

    @PostMapping("/{page}/pack/{pack}")
    public String packEdit(@PathVariable("page") String pageTag,
            @PathVariable("pack") int packId,
            @ModelAttribute("packForm") @Valid DonatePackForm packForm,
            BindingResult binding,
            Model model,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {

        DonatePack pack = find(packs.findById(packId), loc);
        DonatePage page = pack.getCategory().getPage();

        if(!page.getTag().equals(pageTag))
            throw notFound(loc);

        if(!binding.hasErrors()) {

            packForm.apply(pack, donateService, categories, servers);

            pack = packs.save(pack);

            log("admin.log.donate.pack.edit", request, pack.getTitle()+" #"+pack.getId());
            popup("admin.donate.pack.edited", loc, PopupMessage.Type.OK, request, response);
        }

        model.addAttribute("method", "post");
        model.addAttribute("page", page);
        model.addAttribute("pack", pack);
        model.addAttribute("token_types", DonatePackTokenType.values());

        return "admin/donate/pack_form";
    }

    @DeleteMapping("/{page}/pack/{pack}")
    public String deletePack(@PathVariable("page") String pageTag,
            @PathVariable("pack") int packId,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {

        DonatePack pack = find(packs.findById(packId), loc);
        DonatePage page = pack.getCategory().getPage();

        if(!page.getTag().equals(pageTag))
            throw notFound(loc);

        packs.delete(pack);

        log("admin.log.donate.pack.delete", request, pack.getTitle()+" #"+pack.getId());
        popup("admin.donate.pack.deleted", loc, PopupMessage.Type.WARN, request, response);

        return "redirect:/admin/donate/pages/"+page.getTag();
    }

    //UTILS

    /*
    private <T> T find(T searchResult, Locale loc) {
        return find(Optional.ofNullable(searchResult), loc);
    }
    */

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
