package ru.pixelmongo.pixelmongo.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import ru.pixelmongo.pixelmongo.exceptions.TooMuchLoginAttemptsException;
import ru.pixelmongo.pixelmongo.services.LoginAttemptService;

public class LoginAttemptFilter extends GenericFilterBean{

    private final LoginAttemptService attemptService;
    private final RequestMatcher requiresAuthenticationRequestMatcher;
    private AuthenticationFailureHandler authFailHandler;

    public LoginAttemptFilter(LoginAttemptService service, String loginProcessUrl,
            AuthenticationFailureHandler failHandler) {
        this.attemptService = service;
        requiresAuthenticationRequestMatcher = new AntPathRequestMatcher(loginProcessUrl, "POST");
        this.authFailHandler = failHandler;
    }

    public void setAuthFailHandler(AuthenticationFailureHandler authFailHandler) {
        this.authFailHandler = authFailHandler;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        doHTTPFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doHTTPFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException{
        if(requiresAuthenticationRequestMatcher.matches((HttpServletRequest) request)
                && attemptService.isBlocked(request)) {
            userBlocked(request, response);
            return;
        }
        chain.doFilter(request, response);
    }

    private void userBlocked(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        TooMuchLoginAttemptsException ex = new TooMuchLoginAttemptsException();
        this.logger.trace("Failed to process authentication request", ex);
        this.authFailHandler.onAuthenticationFailure(request, response, ex);
    }

}
