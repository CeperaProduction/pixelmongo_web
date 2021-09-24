package ru.pixelmongo.pixelmongo.controllers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.exceptions.InvalidCaptchaEcxeption;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ValidationErrorMessage;

/**
 * Extend this to add restful default exception handlers
 *
 */
public class RestControllerExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    private MessageSource msg;

    public RestControllerExceptionHandler(MessageSource msg) {
        this.msg = msg;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResultMessage handleStatusException(ResponseStatusException ex, HttpServletResponse response) {
        response.setStatus(ex.getRawStatusCode());
        return new ResultMessage(DefaultResult.ERROR, ex.getMessage());
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
    public ResultMessage handleCaptchaException(InvalidCaptchaEcxeption ex, Locale loc){
        return new ResultMessage(DefaultResult.ERROR, msg.getMessage("captcha.fail", null, loc));
    }

}
