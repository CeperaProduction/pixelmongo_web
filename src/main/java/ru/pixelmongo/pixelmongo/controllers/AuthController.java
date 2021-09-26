package ru.pixelmongo.pixelmongo.controllers;

import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.pixelmongo.pixelmongo.exceptions.UserAlreadyExistsException;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.forms.UserRegistrationForm;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ValidationErrorMessage;
import ru.pixelmongo.pixelmongo.services.CaptchaService;
import ru.pixelmongo.pixelmongo.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserService userService;

    @Autowired
    private RememberMeServices rememberMe;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private MessageSource msg;

    //mappings

    @PutMapping(path = "/register")
    public ResultMessage register(HttpServletRequest request,
            HttpServletResponse response,
            @Valid UserRegistrationForm form,
            Locale loc) throws ServletException {
        if(!form.getPassword().equals(form.getPasswordRepeat()))
            return new ValidationErrorMessage(msg.getMessage("auth.password.not_same", null, loc),
                    new String[] {"passwordRepeat"},
                    new String[] {msg.getMessage("auth.password.not_same", null, loc)});
        if(isLoggedIn())
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("auth.logged.already", null, loc));

        String ip = request.getRemoteAddr();

        String captcha = request.getParameter("g-recaptcha-response");
        captchaService.processResponse(captcha, ip);

        User user = userService.registerUser(form.getLogin(), form.getEmail(), form.getPassword(), ip);
        LOGGER.debug("User "+user.getName()+" registered with IP "+ip);

        login(request, response, form.getLogin(), form.getPassword());


        return new ResultMessage(DefaultResult.OK, msg.getMessage("auth.register.success", null, loc));
    }

    //utils

    private boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && !(auth instanceof AnonymousAuthenticationToken);
    }

    private Authentication login(HttpServletRequest request,
            HttpServletResponse response,
            String login,
            String password) {
        UsernamePasswordAuthenticationToken tocken = new UsernamePasswordAuthenticationToken(login, password);
        tocken.setDetails(new WebAuthenticationDetails(request));
        Authentication auth = authManager.authenticate(tocken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        rememberMe.loginSuccess(request, response, auth);
        userService.saveLoginData(auth, request.getRemoteAddr());
        return auth;
    }

    //exception handlers

    @ExceptionHandler
    public ResultMessage handleUserAlreadyExistsException(UserAlreadyExistsException ex, Locale loc){
        return new ResultMessage(DefaultResult.ERROR, msg.getMessage("auth.register.already_exists", null, loc));
    }

}
