package ru.pixelmongo.pixelmongo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.pixelmongo.pixelmongo.exceptions.UserAlreadyExistsException;
import ru.pixelmongo.pixelmongo.model.entities.User;
import ru.pixelmongo.pixelmongo.model.entities.UserDetails;
import ru.pixelmongo.pixelmongo.model.entities.UserGroup;
import ru.pixelmongo.pixelmongo.model.entities.UserLoginRecord;
import ru.pixelmongo.pixelmongo.repositories.UserGroupRepository;
import ru.pixelmongo.pixelmongo.repositories.UserLoginRecordRepository;
import ru.pixelmongo.pixelmongo.repositories.UserRepository;

@Service
class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository users;

    @Autowired
    private UserGroupRepository groups;

    @Autowired
    private UserLoginRecordRepository loginRecords;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserDetails(String name) throws UsernameNotFoundException {
        User user = users.findByName(name)
                .orElseThrow(()->new UsernameNotFoundException("User "+name+" not found!"));
        return new UserDetails(user, makeAuthority(user));
    }

    @Override
    public List<GrantedAuthority> makeAuthority(User user){
        List<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
        UserGroup group = user.getGroup();
        if(group != null) {
            auth.add(new SimpleGrantedAuthority("GROUP_"+group.getId()));
            getCustomGroupTag(group).ifPresent(tag->
                auth.add(new SimpleGrantedAuthority("GROUP_"+tag)));
            auth.addAll(group.getPermissions());
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
    public Optional<User> getUser(UserDetails userDetails) {
        return users.findById(userDetails.getUserId());
    }

    @Override
    public User registerUser(String name, String email, String password,
            String registerIp) throws UserAlreadyExistsException{
        if(users.findByName(name).isPresent())
            throw new UserAlreadyExistsException("User with this name is already registered!");
        if(users.findByEmail(email).isPresent())
            throw new UserAlreadyExistsException("User with this email is already registered!");
        password = passwordEncoder.encode(password);
        User user = new User(name, groups.findById(2).get(), email, password);
        return users.save(user);
    }

    @Override
    public void saveLoginData(User user, String ip) {
        UserLoginRecord record = new UserLoginRecord(user, ip);
        user.getLoginRecords().add(record);
        loginRecords.save(record);
    }

}
