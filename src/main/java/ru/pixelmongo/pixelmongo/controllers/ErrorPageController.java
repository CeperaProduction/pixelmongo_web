package ru.pixelmongo.pixelmongo.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import ru.pixelmongo.pixelmongo.configs.MvcConfig;

@Controller
public class ErrorPageController implements ErrorController{

    private Logger logger = LogManager.getLogger();

    @Autowired
    private MessageSource msg;

    @Autowired
    private MvcConfig mvcConfig;

    private List<String> imgTypes = Arrays.asList(".jpg", ".png", ".jpeg", ".gif");

    @RequestMapping("/error")
    public String handleError(@RequestParam(name = "c", required = false) String c,
            HttpServletRequest request, HttpServletResponse response, Model model, Locale loc) {
        Object status = c != null ? c : request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.valueOf(status.toString());
            String uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI).toString();
            if(mvcConfig.isStaticEnabled() && statusCode == 404 && request.getMethod().equals("GET") && isImageRequested(uri)) {
                return "redirect:/static/img/not_found.jpg";
            }

            Object errMsgObj = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            if(errMsgObj == null) errMsgObj = "";
            String message = msg.getMessage("error.status."+statusCode, null, errMsgObj.toString(), loc);

            if(message.isEmpty()) {
                Object ex = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
                if(ex != null) message = ex.toString();
            }

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

    private boolean isImageRequested(String uri) {
        uri = uri.toLowerCase();
        if(uri.equals("/img/not_found.jpg") || uri.contains("/skins/")) return false;
        return imgTypes.stream().anyMatch(uri::endsWith);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception e) {
        logger.catching(e);
        return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
