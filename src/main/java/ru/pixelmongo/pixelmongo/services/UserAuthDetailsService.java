package ru.pixelmongo.pixelmongo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthDetailsService implements UserDetailsService{

    @Autowired
    private UserService authService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return authService.loadUserByUsername(username);
    }

}
