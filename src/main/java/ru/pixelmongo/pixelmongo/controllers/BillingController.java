package ru.pixelmongo.pixelmongo.controllers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.pixelmongo.pixelmongo.model.dao.primary.BillingData;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.BillingDataRepository;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/pay")
public class BillingController {

    private static final List<String> ID_KEYS = Arrays.asList("pay_id");

    @Autowired
    private UserService userService;

    @Autowired
    private BillingDataRepository payments;

    @Autowired
    private PopupMessageService popups;

    @Autowired
    private MessageSource msg;

    @GetMapping("/success")
    public String success(@RequestParam Map<String, String> allParams, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        BillingData payment = findId(allParams).flatMap(payments::findById).orElse(null);
        if(payment != null) {

            switch(payment.getStatus()) {
            case BillingData.STATUS_DONE:
                popups.sendUsingCookies(new PopupMessage(
                        msg.getMessage("billing.pay.success", new Object[] {payment.getUserName()}, loc),
                        PopupMessage.Type.OK),
                    request, response);
                break;
            case BillingData.STATUS_ERROR:
                popups.sendUsingCookies(new PopupMessage(
                        msg.getMessage("billing.pay.fail", null, loc), PopupMessage.Type.WARN),
                    request, response);
                break;
            default:
                popups.sendUsingCookies(new PopupMessage(
                        msg.getMessage("billing.pay.processing", null, loc), PopupMessage.Type.INFO),
                    request, response);
                break;
            }

        }
        return "redirect:/profile";
    }

    @GetMapping("/fail")
    public String fail(@RequestParam Map<String, String> allParams, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        BillingData payment = findId(allParams).flatMap(payments::findById).orElse(null);
        if(payment != null && payment.getStatus() != BillingData.STATUS_DONE) {
            popups.sendUsingCookies(new PopupMessage(
                        msg.getMessage("billing.pay.fail", null, loc), PopupMessage.Type.WARN),
                    request, response);
            if(payment.getStatus() != BillingData.STATUS_ERROR
                    && userService.getCurrentUser().getId() == payment.getUserId()) {
                payment.setUpdated(new Date());
                payment.setMessage("User redirected to fail page");
                payment.setStatus(BillingData.STATUS_ERROR);
                payments.save(payment);
            }
        }
        return "redirect:/profile";
    }

    private Optional<Integer> findId(Map<String, String> allParams) {
        for(String key : ID_KEYS) {
            String val = allParams.get(key);
            if(val != null) {
                try {
                    return Optional.of(Integer.parseInt(val));
                }catch(Exception ex) {}
            }
        }
        return Optional.empty();
    }

}
