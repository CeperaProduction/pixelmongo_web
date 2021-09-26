package ru.pixelmongo.pixelmongo.controllers;

import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorPageController implements ErrorController{

    @Autowired
    private MessageSource msg;

    @RequestMapping("/error")
    public String handleError(@RequestParam(name = "c", required = false) String c,
            HttpServletRequest request, Model model, Locale loc) {
        Object status = c != null ? c : request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            String uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI).toString();

            String message = msg.getMessage("error.status."+statusCode, null,
                    request.getAttribute(RequestDispatcher.ERROR_MESSAGE).toString(), loc);

            model.addAttribute("status", statusCode);
            model.addAttribute("message", message);

            String template = "error";

            if(uri.startsWith("/admin")) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                boolean access = auth != null
                        && !(auth instanceof AnonymousAuthenticationToken)
                        && auth.getAuthorities().stream()
                        .anyMatch(p->p.getAuthority().equals("admin.panel.access"));
                if(access) {
                    template = "admin/error";
                }
            }

            return template;
        }
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception e) {
        return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
