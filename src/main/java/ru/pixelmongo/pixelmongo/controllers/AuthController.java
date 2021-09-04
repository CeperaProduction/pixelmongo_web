package ru.pixelmongo.pixelmongo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.pixelmongo.pixelmongo.model.transport.DefaultResult;
import ru.pixelmongo.pixelmongo.model.transport.ResultMessage;

@RestController
@RequestMapping("/auth")
@RestControllerAdvice
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @GetMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultMessage login(@RequestParam String login, @RequestParam String password) {
        Authentication authenticationToken =
                new UsernamePasswordAuthenticationToken(login, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResultMessage(DefaultResult.OK, "Name: "+authentication.getName());
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResultMessage> handleAuthenticationException(AuthenticationException ex){
        return new ResponseEntity<ResultMessage>(
                new ResultMessage(DefaultResult.ERROR, ex.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResultMessage> handleMissingParametersException(MissingServletRequestParameterException ex){
        return new ResponseEntity<ResultMessage>(
                new ResultMessage(DefaultResult.ERROR, ex.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultMessage> handleUnexpectedException(Exception ex){
        return new ResponseEntity<ResultMessage>(
                new ResultMessage(DefaultResult.ERROR, ex.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
