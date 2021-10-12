package ru.pixelmongo.pixelmongo.controllers.open;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.pixelmongo.pixelmongo.services.BillingService;

@RestController
@RequestMapping("/open/billing")
public class BillingGatewayController {

    @Autowired
    private BillingService billing;

    @RequestMapping(value = "/{handler}", method = {RequestMethod.GET, RequestMethod.POST})
    public Object handleWebHook(@PathVariable("handler") String handlerName,
            @RequestParam Map<String, String> allParams, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        return billing.handleWebHook(handlerName, allParams, loc, request, response);
    }

}
