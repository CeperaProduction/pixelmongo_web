package ru.pixelmongo.pixelmongo.services.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.pixelmongo.pixelmongo.exceptions.UserAlreadyExistsException;
import ru.pixelmongo.pixelmongo.model.UserDetails;
import ru.pixelmongo.pixelmongo.model.dao.User;
import ru.pixelmongo.pixelmongo.model.dao.UserGroup;
import ru.pixelmongo.pixelmongo.model.dao.UserLoginRecord;
import ru.pixelmongo.pixelmongo.repositories.UserGroupRepository;
import ru.pixelmongo.pixelmongo.repositories.UserLoginRecordRepository;
import ru.pixelmongo.pixelmongo.repositories.UserPermissionRepository;
import ru.pixelmongo.pixelmongo.repositories.UserRepository;
import ru.pixelmongo.pixelmongo.services.UserService;

@Service("userService")
class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository users;

    @Autowired
    private UserGroupRepository groups;

    @Autowired
    private UserLoginRecordRepository loginRecords;

    @Autowired
    private UserPermissionRepository permissions;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = users.findByName(name)
                .orElseThrow(()->new UsernameNotFoundException("User "+name+" not found!"));
        UserDetails details = new UserDetails(user, makeAuthority(user));
        return details;
    }

    @Override
    public List<GrantedAuthority> makeAuthority(User user){
        List<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
        UserGroup group = user.getGroup();
        if(group != null) {
            auth.add(new SimpleGrantedAuthority("GROUP_"+group.getId()));
            getCustomGroupTag(group).ifPresent(tag->
                auth.add(new SimpleGrantedAuthority("GROUP_"+tag)));
            if(group.getId() == UserGroupRepository.GROUP_ID_ADMIN) {
                permissions.findAll().forEach(auth::add);
            }else {
                auth.addAll(group.getPermissions());
            }
        }
        return auth;
    }

    private Optional<String> getCustomGroupTag(UserGroup group){
        switch(group.getId()) {
        case UserGroupRepository.GROUP_ID_ADMIN: return Optional.of("ADMIN");
        case UserGroupRepository.GROUP_ID_USER: return Optional.of("USER");
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
            throw new UserAlreadyExistsException("User with name "+name+" is already registered!");
        if(users.findByEmail(email).isPresent())
            throw new UserAlreadyExistsException("User with email "+email+" is already registered!");
        password = passwordEncoder.encode(password);
        User user = new User(name, groups.findById(2).get(), email, password, registerIp);
        return users.save(user);
    }

    @Override
    public void saveLoginData(User user, String ip) {
        UserLoginRecord record = new UserLoginRecord(user, ip);
        user.getLoginRecords().add(record);
        loginRecords.save(record);
    }

    @Override
    public Optional<UserLoginRecord> getLastLogin(User user) {
        Iterator<UserLoginRecord> it = user.getLoginRecords().iterator();
        if(it.hasNext()) return Optional.of(it.next());
        return Optional.empty();
    }

    @Override
    public void changePassword(User user, Object principal, String newPassword, boolean saveUser) {
        final String password = passwordEncoder.encode(newPassword);
        user.setPassword(password);
        if(principal == null) {

        }
        if(principal instanceof UserDetails && ((UserDetails) principal).getUsername().equals(user.getName())) {
            ((UserDetails) principal).setPassword(password);
        }
        if(saveUser) users.save(user);
    }

    @Override
    public void updateAuthorities(User user, Object principal) {
        getCurrentDetails().filter(d->d.getUsername().equals(user.getName()))
            .ifPresent(d->d.setAuthorities(makeAuthority(user)));
    }

    private Optional<UserDetails> getCurrentDetails(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() instanceof UserDetails) {
            return Optional.of((UserDetails) auth.getPrincipal());
        }
        return Optional.empty();
    }

}
