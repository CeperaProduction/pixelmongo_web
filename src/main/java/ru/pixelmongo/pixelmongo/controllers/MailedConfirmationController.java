package ru.pixelmongo.pixelmongo.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.pixelmongo.pixelmongo.exceptions.MailedConfirmationExpiredException;
import ru.pixelmongo.pixelmongo.exceptions.MailedConfirmationNotFoundException;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.services.MailedConfirmationService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;

@Controller
@RequestMapping("/mconfirm")
public class MailedConfirmationController {

    @Autowired
    private MailedConfirmationService confirmation;

    @Autowired
    private PopupMessageService popup;

    @Autowired
    private MessageSource msg;

    @GetMapping(params = "key")
    public String process(@RequestParam("key") String key, Locale loc, HttpServletRequest request,
            HttpServletResponse response) {
        String redirect = "/";
        String msg = null;
        try {
            redirect = confirmation.processConfirmation(key, loc, request, response);
        }catch (MailedConfirmationNotFoundException ex) {
            msg = this.msg.getMessage("mconfirm.error.not_found", null, loc);
        }catch (MailedConfirmationExpiredException ex) {
            msg = this.msg.getMessage("mconfirm.error.expired", null, loc);
        }catch (Exception ex) {
            msg = ex.getMessage().isEmpty() ? ex.toString() : ex.getMessage();
        }
        if(msg != null) {
            popup.sendUsingCookies(new PopupMessage(msg, PopupMessage.Type.ERROR), request, response);
        }
        return "redirect:"+redirect;
    }

}
