package ru.pixelmongo.pixelmongo.services;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ru.pixelmongo.pixelmongo.exceptions.UserAlreadyExistsException;
import ru.pixelmongo.pixelmongo.model.User;

public interface UserService {

    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException;

    public List<GrantedAuthority> getAuthority(User user);
    
    public UserDetails registerUser(User user) throws UserAlreadyExistsException;
    
}
