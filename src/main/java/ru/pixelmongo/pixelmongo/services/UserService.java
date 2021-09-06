package ru.pixelmongo.pixelmongo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ru.pixelmongo.pixelmongo.exceptions.UserAlreadyExistsException;
import ru.pixelmongo.pixelmongo.model.entities.User;
import ru.pixelmongo.pixelmongo.model.entities.UserDetails;

public interface UserService {

    public UserDetails loadUserDetails(String name) throws UsernameNotFoundException;

    public Optional<User> getUser(UserDetails userDetails);

    public List<GrantedAuthority> makeAuthority(User user);

    public User registerUser(String name, String email, String password,
            String registerIp) throws UserAlreadyExistsException;

}
