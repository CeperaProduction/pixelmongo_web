package ru.pixelmongo.pixelmongo.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.pixelmongo.pixelmongo.exceptions.InvalidCaptchaEcxeption;
import ru.pixelmongo.pixelmongo.model.entities.UserDetails;
import ru.pixelmongo.pixelmongo.model.entities.forms.UserLoginForm;
import ru.pixelmongo.pixelmongo.model.entities.forms.UserRegistrationForm;
import ru.pixelmongo.pixelmongo.model.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.model.results.ResultMessage;
import ru.pixelmongo.pixelmongo.services.CaptchaService;
import ru.pixelmongo.pixelmongo.services.UserService;

@RestController
@RequestMapping("/auth")
@RestControllerAdvice
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CaptchaService captchaService;

    //mappings

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultMessage login(HttpServletRequest request, UserLoginForm user) {
        if(isLoggedIn())
            return new ResultMessage(DefaultResult.ERROR, "Already logged in!");
        Authentication auth = doLogin(user.getLogin(), user.getPassword(), request.getRemoteAddr());
        return new ResultMessage(DefaultResult.OK, "Name: "+auth.getName());
    }

    @PostMapping(path = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultMessage logout(HttpServletRequest request, HttpServletResponse response) {
        if(!isLoggedIn()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return new ResultMessage(DefaultResult.ERROR, "Not logged in!");
        }
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, null, null);
        return new ResultMessage(DefaultResult.OK, "Logged out.");
    }

    @PostMapping(path = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultMessage register(HttpServletRequest request, @Valid UserRegistrationForm form) {
        if(!form.getPassword().equals(form.getPasswordRepeat()))
            return new ResultMessage(DefaultResult.ERROR, "Passwords are not equal.");
        if(isLoggedIn())
            return new ResultMessage(DefaultResult.ERROR, "Already logged in! Can't do registration.");

        String ip = request.getRemoteAddr();

        String captcha = request.getParameter("g-recaptcha-response");
        captchaService.processResponse(captcha, ip);

        userService.registerUser(form.getLogin(), form.getEmail(), form.getPassword(), ip);
        doLogin(form.getLogin(), form.getPassword(), ip);

        return new ResultMessage(DefaultResult.OK, "User registered and logged in");
    }

    //utils

    private boolean isLoggedIn() {
        SecurityContext sc = SecurityContextHolder.getContext();
        Authentication auth = sc.getAuthentication();
        return auth != null && !(auth instanceof AnonymousAuthenticationToken);
    }

    private Authentication doLogin(String login, String password, String ip) {
        Authentication auth = new UsernamePasswordAuthenticationToken(login, password);
        auth = authenticationManager.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(auth);
        userService.getUser((UserDetails) auth.getPrincipal()).ifPresent(user ->
                    userService.saveLoginData(user, ip));
        return auth;
    }

    //exception handlers

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ResultMessage handleAuthenticationException(AuthenticationException ex){
        return new ResultMessage(DefaultResult.ERROR, ex.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultMessage handleMissingParametersException(MissingServletRequestParameterException ex){
        return new ResultMessage(DefaultResult.ERROR, ex.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResultMessage handleUnexpectedException(Exception ex){
        return new ResultMessage(DefaultResult.ERROR, ex.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDataMessage<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((err) -> {
            String field = ((FieldError) err).getField();
            String msg = err.getDefaultMessage();
            errors.put(field, msg);
        });
        return new ResultDataMessage<>(DefaultResult.ERROR, "Validation error", errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidCaptchaEcxeption.class)
    public ResultMessage handleMissingParametersException(InvalidCaptchaEcxeption ex){
        return new ResultMessage(DefaultResult.ERROR, ex.getLocalizedMessage());
    }

}
