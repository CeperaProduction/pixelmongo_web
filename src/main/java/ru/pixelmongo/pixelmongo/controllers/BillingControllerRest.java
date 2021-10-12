package ru.pixelmongo.pixelmongo.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.services.BillingService;
import ru.pixelmongo.pixelmongo.services.UserService;

@RestController
@RequestMapping("/pay")
public class BillingControllerRest {

    @Autowired
    private UserService userService;

    @Autowired
    private BillingService billing;

    @PostMapping("/form")
    public ResultMessage makeForm(@RequestParam("handler") String handlerName,
            @RequestParam("sum") int sum, Locale loc, HttpServletRequest request,
            HttpServletResponse response) {

        User user = userService.getCurrentUser();
        if(user.isAnonymous())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        if(sum < 100)
            return new ResultMessage(DefaultResult.ERROR, "Minimal sum is 100");

        return billing.makePaymentForm(handlerName, user, sum, loc, request, response);

    }

}
