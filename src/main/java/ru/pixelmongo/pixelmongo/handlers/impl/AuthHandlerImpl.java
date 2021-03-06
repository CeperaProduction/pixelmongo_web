package ru.pixelmongo.pixelmongo.handlers.impl;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.pixelmongo.pixelmongo.exceptions.TooMuchLoginAttemptsException;
import ru.pixelmongo.pixelmongo.handlers.AuthHandler;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.services.LoginAttemptService;
import ru.pixelmongo.pixelmongo.services.UserService;

public class AuthHandlerImpl implements AuthHandler{

    @Autowired
    private UserService userService;

    @Autowired
    private LoginAttemptService loginAttempts;

    @Autowired
    private MessageSource msg;

    private ObjectMapper json = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        setEncoding(response);
        ResultMessage result = new ResultMessage(DefaultResult.OK,
                msg.getMessage("auth.logged.in", null, request.getLocale()));
        response.getWriter().println(json.writeValueAsString(result));
        userService.saveLoginData(authentication, request.getRemoteAddr());
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String message;
        if(exception instanceof TooMuchLoginAttemptsException) {
            message = msg.getMessage("auth.fail.blocked", null, request.getLocale());
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }else if(exception instanceof UsernameNotFoundException
                || exception instanceof BadCredentialsException) {
            message = msg.getMessage("auth.fail", null, request.getLocale());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            loginAttempts.onLoginFail(request);
        }else {
            message = exception.toString();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        setEncoding(response);
        ResultMessage result = new ResultMessage(DefaultResult.ERROR, message);
        response.getWriter().println(json.writeValueAsString(result));
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {
        setEncoding(response);
        ResultMessage result = new ResultMessage(DefaultResult.OK,
                msg.getMessage("auth.logged.out", null, request.getLocale()));
        response.getWriter().println(json.writeValueAsString(result));
    }

    private void setEncoding(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.FORBIDDEN.value()+"");
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, request.getRequestURI());
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, authException.getMessage());
        request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, authException);
        RequestDispatcher dispatcher = request.getServletContext()
                .getRequestDispatcher("/error");
        dispatcher.forward(request, response);
    }

}
