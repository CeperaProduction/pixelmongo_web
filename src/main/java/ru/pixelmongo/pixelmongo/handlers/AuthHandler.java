package ru.pixelmongo.pixelmongo.handlers;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

public interface AuthHandler extends AuthenticationFailureHandler,
        AuthenticationSuccessHandler, LogoutSuccessHandler, AuthenticationEntryPoint{

}
