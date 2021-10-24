package ru.pixelmongo.pixelmongo.handlers;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;

public interface BillingHandler {

    public boolean isEnabled();

    public int getPriority();

    public String getName();

    /**
     * Generate initial billing data and return suggested information for user
     * @param user
     * @param params
     * @param loc
     * @param request
     * @param response
     * @return normally is {@link ResultDataMessage} with status 'OK' containing 'location' with form redirect url
     */
    public ResultMessage makeForm(User user, int sum, Locale loc, HttpServletRequest request,
            HttpServletResponse response);

    /**
     * Gateway for billing notifications
     * @param params
     * @param request
     * @param response
     * @return
     */
    public Object processWebHook(Map<String, String> params, Locale loc,
            HttpServletRequest request, HttpServletResponse response);

}
