package ru.pixelmongo.pixelmongo.controllers.admin.donate;

import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.exceptions.DonatePackActiveException;
import ru.pixelmongo.pixelmongo.exceptions.DonatePackTokenException;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateServer;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.model.dto.forms.donate.DonatePackGiveForm;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonatePackRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateServerRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.DonateService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.TemplateService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/admin/donate/give")
public class DonateGiveController {

    @Autowired
    private UserRepository users;

    @Autowired
    private DonatePackRepository packs;

    @Autowired
    private DonateServerRepository servers;

    @Autowired
    private DonateService donateService;

    @Autowired
    private AdminLogService logs;

    @Autowired
    private MessageSource msg;

    @Autowired
    private PopupMessageService popup;

    @Autowired
    private UserService userService;

    @Autowired
    private TemplateService tpl;

    @GetMapping(params = "!pack")
    public String choosePack(Model model) {
        model.addAttribute("packs", packs.findAll());
        return "admin/donate/give_choose_pack";
    }

    @GetMapping(params = "pack")
    public String redirect(@RequestParam("pack") int packId) {
        return "redirect:/admin/donate/give/"+packId;
    }

    @GetMapping("/{pack}")
    public String give(@PathVariable("pack") int packId, Model model, Locale loc) {
        DonatePack pack = find(packs.findById(packId), loc);
        model.addAttribute("pack", pack);
        model.addAttribute("servers", servers.findAll());
        model.addAttribute("giveForm", new DonatePackGiveForm());
        return "admin/donate/give";
    }

    @PostMapping("/{pack}")
    public String give(@PathVariable("pack") int packId,
            @ModelAttribute("giveForm") DonatePackGiveForm giveForm,
            BindingResult binding,
            Model model,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {
        DonatePack pack = find(packs.findById(packId), loc);

        DonateServer server = servers.findById(giveForm.getServer()).orElse(null);
        if(server == null) {
            binding.addError(new FieldError("giveForm", "server",
                    msg.getMessage("error.status.404.donate_server", null, loc)));
        }

        User user = users.findByName(giveForm.getUser()).orElse(null);
        if(user == null) {
            binding.addError(new FieldError("giveForm", "user",
                    msg.getMessage("error.status.404.user", null, loc)));
        }

        if(!binding.hasErrors()) {
            try {
                int given = donateService.buyPack(pack, user, server, giveForm.getTokens(), giveForm.getCount(), true);
                String packTitle = pack.getTitle();
                if(given > 1) packTitle += " x"+given;
                log("admin.log.donate.pack.give", request, packTitle, user.getName(), server.getDisplayName());
                popup("admin.donate.pack.given", loc, PopupMessage.Type.OK, request, response);
                return "redirect:/admin/donate/give/"+pack.getId();
            }catch(DonatePackTokenException ex) {
                DonateService.LOGGER.catching(ex);
                binding.addError(new FieldError("giveForm", "tokens", ex.getMessage()));
            }catch(DonatePackActiveException ex) {
                String message;
                if(ex.getBackExecuteDate().getTime() > System.currentTimeMillis()) {
                    message = msg.getMessage("donate.pack.active",
                            new Object[] {tpl.printDate(ex.getBackExecuteDate(), loc)}, loc);
                }else {
                    message = msg.getMessage("donate.pack.active.now", null, loc);
                }
                binding.addError(new ObjectError("giveForm", message));
            }
        }


        model.addAttribute("pack", pack);
        model.addAttribute("servers", servers.findAll());
        return "admin/donate/give";
    }


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
