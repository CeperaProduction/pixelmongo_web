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
    private List<BillingHandler> ordered = new ArrayList<>();

    @Autowired
    public void setupHandlers(List<BillingHandler> handlers) {
        handlers.forEach(h->billings.put(h.getName(), h));
        this.ordered = new ArrayList<>(handlers);
        Collections.sort(this.ordered, (h1, h2)->Integer.compare(h2.getPriority(), h1.getPriority()));
        LOGGER.info("Loaded "+billings.size()+" billing handlers: "
                + String.join(",", billings.keySet().toArray(new String[billings.size()])));

    }

    @Override
    public Optional<BillingHandler> getHandler(String handlerName){
        return Optional.ofNullable(billings.get(handlerName));
    }

    @Override
    public List<String> getHandlerNames(){
        ArrayList<String> handlerNames = new ArrayList<>();
        this.ordered.forEach(h->handlerNames.add(h.getName()));
        return Collections.unmodifiableList(handlerNames);
    }

    @Override
    public List<BillingHandler> getHandlers(){
        return Collections.unmodifiableList(ordered);
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
    public Object handleWebHook(String handlerName, Map<String, String> params, String body, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        BillingHandler handler = billings.get(handlerName);
        if(handler == null)
            throw new BillingHandlerNotFoundException(handlerName);
        if(!handler.isEnabled())
            throw new BillingHandlerDisabledException(handlerName);
        return handler.processWebHook(params, body, loc, request, response);
    }

}
