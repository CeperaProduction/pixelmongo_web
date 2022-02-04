package ru.pixelmongo.pixelmongo.handlers;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;

public interface RatingHandler {

    public boolean isEnabled();

    public String getName();

    public default String getTemplateFragmentName() {
        return getName().toLowerCase();
    }

    public default boolean useJson() {
        return false;
    }

    public boolean isValidRequest(Map<String, String> params, Locale loc, HttpServletRequest request);

    public String getUserName(Map<String, String> params, Locale loc, HttpServletRequest request);

    public default Object getSuccessResult(User user, Map<String, String> params, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        String message = getSuccessMessage(user, params, loc, request, response);
        return useJson() ? new ResultMessage(DefaultResult.OK, message) : message;
    }

    public default String getSuccessMessage(User user, Map<String, String> params, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        return user.getName()+" voted successfuly";
    }

    public default Object getInvalidRequestResult(Map<String, String> params, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        String message = getInvalidRequestMessage(params, loc, request, response);
        return useJson() ? new ResultMessage(DefaultResult.ERROR, message) : message;
    }

    public default String getInvalidRequestMessage(Map<String, String> params, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        return "Invalid signature";
    }

    public default Object getAlreadyVotedResult(User user, Map<String, String> params, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        //response.setStatus(HttpStatus.CONFLICT.value());
        String message = getAlreadyVotedMessage(user, params, loc, request, response);
        return useJson() ? new ResultMessage(DefaultResult.ERROR, message) : message;
    }

    public default String getAlreadyVotedMessage(User user, Map<String, String> params, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        return user.getName()+" already voted today";
    }

    public default Object getUserNotFoundResult(@Nullable String userName, Map<String, String> params,
            Locale loc, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        String message = getUserNotFoundMessage(userName, params, loc, request, response);
        return useJson() ? new ResultMessage(DefaultResult.ERROR, message) : message;
    }

    public default String getUserNotFoundMessage(@Nullable String userName, Map<String, String> params,
            Locale loc, HttpServletRequest request, HttpServletResponse response) {
        return "User not found";
    }

}
