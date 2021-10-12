package ru.pixelmongo.pixelmongo.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import ru.pixelmongo.pixelmongo.exceptions.BillingHandlerDisabledException;
import ru.pixelmongo.pixelmongo.exceptions.BillingHandlerNotFoundException;
import ru.pixelmongo.pixelmongo.handlers.BillingHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.services.BillingService;

public class BillingServiceImpl implements BillingService{

    private Map<String, BillingHandler> billings = new HashMap<>();

    @Autowired
    public void setupHandlers(List<BillingHandler> handlers) {
        handlers.forEach(h->billings.put(h.getName(), h));
        LOGGER.info("Loaded "+billings.size()+" billing handlers: "
                + String.join(",", billings.keySet().toArray(new String[billings.size()])));
    }

    @Override
    public Optional<BillingHandler> getHandler(String handlerName){
        return Optional.ofNullable(billings.get(handlerName));
    }

    @Override
    public List<String> getHandlerNames(){
        return Collections.unmodifiableList(new ArrayList<>(billings.keySet()));
    }

    @Override
    public List<BillingHandler> getHandlers(){
        return Collections.unmodifiableList(new ArrayList<>(billings.values()));
    }

    @Override
    public ResultMessage makePaymentForm(String handlerName, User user, int sum, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        BillingHandler handler = billings.get(handlerName);
        if(handler == null)
            throw new BillingHandlerNotFoundException(handlerName);
        if(!handler.isEnabled())
            throw new BillingHandlerDisabledException(handlerName);
        return handler.makeForm(user, sum, loc, request, response);
    }

    @Override
    public Object handleWebHook(String handlerName, Map<String, String> params, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        BillingHandler handler = billings.get(handlerName);
        if(handler == null)
            throw new BillingHandlerNotFoundException(handlerName);
        if(!handler.isEnabled())
            throw new BillingHandlerDisabledException(handlerName);
        return handler.processWebHook(params, loc, request, response);
    }

}
