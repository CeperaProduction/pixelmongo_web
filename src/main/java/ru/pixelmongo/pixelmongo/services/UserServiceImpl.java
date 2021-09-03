package ru.pixelmongo.pixelmongo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.pixelmongo.pixelmongo.exceptions.UserAlreadyExistsException;
import ru.pixelmongo.pixelmongo.model.User;
import ru.pixelmongo.pixelmongo.model.UserGroup;
import ru.pixelmongo.pixelmongo.repositories.UserGroupRepository;
import ru.pixelmongo.pixelmongo.repositories.UserRepository;

@Service
class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository users;
    
    @Autowired
    private UserGroupRepository groups;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = users.findByName(name)
                .orElseThrow(()->new UsernameNotFoundException("User "+name+" not found!"));
        return makeUserDetails(user);
    }  
    
    private UserDetails makeUserDetails(User user) {
        return new org.springframework.security.core.userdetails
                .User(user.getName(), user.getPassword(), getAuthority(user));
    }
    
    @Override
    public List<GrantedAuthority> getAuthority(User user){
        List<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
        UserGroup group = user.getGroup();
        if(group != null) {
            auth.add(new SimpleGrantedAuthority("GROUP_"+group.getId()));
            getCustomGroupTag(group).ifPresent(tag->
                auth.add(new SimpleGrantedAuthority("GROUP_"+tag)));
        }
        return auth;
    }
    
    private Optional<String> getCustomGroupTag(UserGroup group){
        switch(group.getId()) {
        case 1: return Optional.of("ADMIN");
        case 2: return Optional.of("USER");
        default: return Optional.empty();
        }
    }
    
    @Override
    public UserDetails registerUser(User user) throws UserAlreadyExistsException{
        if(users.findByName(user.getName()).isPresent())
            throw new UserAlreadyExistsException("User with this name is already registered!");
        if(users.findByEmail(user.getName()).isPresent())
            throw new UserAlreadyExistsException("User with this email is already registered!");
        if(user.getGroup() == null) {
            user.setGroup(groups.findById(2).get());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        users.save(user);
        return makeUserDetails(user);
    }

}
