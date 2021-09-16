package ru.pixelmongo.pixelmongo.repositories.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import ru.pixelmongo.pixelmongo.model.UserDetails;
import ru.pixelmongo.pixelmongo.services.UserService;

public class UserDetailsValidCheckHttpSessionSecurityContextRepository
    extends HttpSessionSecurityContextRepository{

    private final UserService userService;
    private final AbstractRememberMeServices rememberMeService;

    public UserDetailsValidCheckHttpSessionSecurityContextRepository(UserService userService,
            AbstractRememberMeServices rememberMeService) {
        this.userService = userService;
        this.rememberMeService = rememberMeService;
    }

    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        SecurityContext context = super.loadContext(requestResponseHolder);

        Authentication auth = context.getAuthentication();

        if (auth instanceof UsernamePasswordAuthenticationToken && isAuthInvalid(auth)) {
            UserDetails userDetails = userService.loadUserByUsername(auth.getName());
            if(needToUpdate(auth.getPrincipal(), userDetails)) {
                System.out.println("REAUTH LOG PASS");
                UsernamePasswordAuthenticationToken newAuth
                    = new UsernamePasswordAuthenticationToken(
                            userDetails, auth.getCredentials(), userDetails.getAuthorities());
                context.setAuthentication(newAuth);
            }
        }else if(auth instanceof RememberMeAuthenticationToken && isAuthInvalid(auth)) {
            UserDetails userDetails = userService.loadUserByUsername(auth.getName());
            if(needToUpdate(auth.getPrincipal(), userDetails)) {
                System.out.println("REAUTH REMEMBER ME");
                RememberMeAuthenticationToken newAuth = new RememberMeAuthenticationToken(
                        rememberMeService.getKey(), userDetails, userDetails.getAuthorities());
                context.setAuthentication(newAuth);
            }
        }

        return context;
    }

    private boolean isAuthInvalid(Authentication auth) {
        if(auth != null && auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal()).isInvalid();
        }
        return false;
    }

    private boolean needToUpdate(Object principal1, Object principal2) {
        if(principal1 instanceof UserDetails && principal2 instanceof UserDetails) {
            UserDetails details1 = (UserDetails) principal1;
            UserDetails details2 = (UserDetails) principal2;
            if(!details1.getPassword().equals(details2.getPassword()))
                return true;
            int size = details1.getAuthorities().size();
            return size != details2.getAuthorities().size()
                    || intersected(details1.getAuthorities(), details2.getAuthorities()) != size;
        }
        return false;
    }

    private int intersected(Collection<?> c1, Collection<?> c2) {
        List<Object> in = new ArrayList<>();
        for(Object o : c1)
            if(c2.contains(o))
                in.add(o);
        return in.size();
    }



}
