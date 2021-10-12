package ru.pixelmongo.pixelmongo.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.pixelmongo.pixelmongo.exceptions.BillingHandlerDisabledException;
import ru.pixelmongo.pixelmongo.exceptions.BillingHandlerNotFoundException;
import ru.pixelmongo.pixelmongo.handlers.BillingHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;

public interface BillingService {

    public static final Logger LOGGER = LogManager.getLogger(BillingService.class);

    public Optional<BillingHandler> getHandler(String handlerName);

    public List<String> getHandlerNames();

    public List<BillingHandler> getHandlers();

    /**
     * Generate initial billing data and return suggested information for user
     * @param handlerName
     * @param user
     * @param params
     * @param loc
     * @param request
     * @param response
     * @return normally is {@link ResultDataMessage} with status 'OK' containing 'location' with form redirect url
     *
     * @throws #{@link BillingHandlerNotFoundException}, {@link BillingHandlerDisabledException}
     */
    public ResultMessage makePaymentForm(String handlerName, User user, int sum, Locale loc,
            HttpServletRequest request, HttpServletResponse response);

    /**
     * Handle web hook request with given handler
     * @param handlerName
     * @param params
     * @param loc
     * @param request
     * @param response
     * @return
     *
     * @throws #{@link BillingHandlerNotFoundException}, {@link BillingHandlerDisabledException}
     */
    public Object handleWebHook(String handlerName, Map<String, String> params, Locale loc,
            HttpServletRequest request, HttpServletResponse response);

}
