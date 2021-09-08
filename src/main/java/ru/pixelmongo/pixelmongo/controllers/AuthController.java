package ru.pixelmongo.pixelmongo.controllers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import ru.pixelmongo.pixelmongo.exceptions.UserAlreadyExistsException;
import ru.pixelmongo.pixelmongo.model.entities.User;
import ru.pixelmongo.pixelmongo.model.entities.UserDetails;
import ru.pixelmongo.pixelmongo.model.entities.forms.UserLoginForm;
import ru.pixelmongo.pixelmongo.model.entities.forms.UserRegistrationForm;
import ru.pixelmongo.pixelmongo.model.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.model.results.ResultMessage;
import ru.pixelmongo.pixelmongo.model.results.ValidationErrorMessage;
import ru.pixelmongo.pixelmongo.services.CaptchaService;
import ru.pixelmongo.pixelmongo.services.UserService;

@RestController
@RequestMapping("/auth")
@RestControllerAdvice
public class AuthController {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private MessageSource msg;

    //mappings

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultMessage login(HttpServletRequest request, UserLoginForm user, Locale loc) {
        if(isLoggedIn())
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("auth.logged.already", null, loc));
        doLogin(user.getLogin(), user.getPassword(), request.getRemoteAddr());
        return new ResultMessage(DefaultResult.OK, msg.getMessage("auth.logged.in", null, loc));
    }

    @PostMapping(path = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultMessage logout(HttpServletRequest request, HttpServletResponse response, Locale loc) {
        if(!isLoggedIn()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return new ResultMessage(DefaultResult.ERROR,
                    msg.getMessage("auth.logged.not", null, loc));
        }
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, null, null);
        return new ResultMessage(DefaultResult.OK, msg.getMessage("auth.logged.out", null, loc));
    }

    @PostMapping(path = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultMessage register(HttpServletRequest request, @Valid UserRegistrationForm form, Locale loc) {
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

        doLogin(form.getLogin(), form.getPassword(), ip);

        return new ResultMessage(DefaultResult.OK, msg.getMessage("auth.register.success", null, loc));
    }

    //utils

    private boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && !(auth instanceof AnonymousAuthenticationToken);
    }

    private Authentication doLogin(String login, String password, String ip) {
        Authentication auth = new UsernamePasswordAuthenticationToken(login, password);
        auth = authenticationManager.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(auth);
        userService.getUser((UserDetails) auth.getPrincipal()).ifPresent(user -> {
            userService.saveLoginData(user, ip);
            LOGGER.debug(user.getName()+" logged in with IP "+ip);
        });
        return auth;
    }

    //exception handlers

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
        BadCredentialsException.class,
        UsernameNotFoundException.class
        })
    public ResultMessage handleAuthenticationException(AuthenticationException ex, Locale loc){
        return new ResultMessage(DefaultResult.ERROR, msg.getMessage("auth.fail", null, loc));
    }

    @ExceptionHandler
    public ResultMessage handleUserAlreadyExistsException(UserAlreadyExistsException ex, Locale loc){
        return new ResultMessage(DefaultResult.ERROR, msg.getMessage("auth.register.already_exists", null, loc));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResultMessage handleMissingParametersException(MissingServletRequestParameterException ex){
        return new ResultMessage(DefaultResult.ERROR, ex.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResultMessage handleUnexpectedException(Exception ex){
        LOGGER.catching(ex);
        return new ResultMessage(DefaultResult.ERROR, ex.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResultDataMessage<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((err) -> {
            String field = ((FieldError) err).getField();
            String msg = err.getDefaultMessage();
            errors.put(field, msg);
        });
        return new ValidationErrorMessage("Validation error", errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResultMessage handleMissingParametersException(InvalidCaptchaEcxeption ex, Locale loc){
        return new ResultMessage(DefaultResult.ERROR, msg.getMessage("captcha.fail", null, loc));
    }

}
