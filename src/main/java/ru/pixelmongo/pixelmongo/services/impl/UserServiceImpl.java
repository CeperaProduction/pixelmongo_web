package ru.pixelmongo.pixelmongo.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.Transactional;

import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

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

public abstract class UserServiceImpl implements UserService{

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

    private Set<Integer> invalidUserIds = ConcurrentHashMap.newKeySet();
    private Map<Integer, Long> invalidGroupIdsAndTimes = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = users.findByName(name)
                .orElseThrow(()->new UsernameNotFoundException("User "+name+" not found!"));
        return new BindedValidationUserDetails(user, makeAuthority(user));
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
    public void invalidateDetails(User user) {
        invalidUserIds.add(user.getId());
    }

    @Override
    public void invalidateDetails(UserGroup group) {
        invalidGroupIdsAndTimes.put(group.getId(), System.currentTimeMillis());
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
        try {
            user.getLoginRecords().add(record);
        }catch(LazyInitializationException e) {}
        loginRecords.save(record);
    }

    @Override
    public Optional<UserLoginRecord> getLastLogin(User user) {
        Iterator<UserLoginRecord> it = user.getLoginRecords().iterator();
        if(it.hasNext()) return Optional.of(it.next());
        return Optional.empty();
    }

    @Override
    public void changePassword(User user, String newPassword, boolean saveUser) {
        String password = passwordEncoder.encode(newPassword);
        user.setPassword(password);
        if(saveUser) users.save(user);
        invalidateDetails(user);
    }

    @Override
    public boolean hasPerm(String permission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(permission));
    }

    @Override
    public boolean hasPerm(User user, String permission) {
        if(user != null) {
            return user.getGroup().getPermissions().stream()
                .anyMatch(p->p.getAuthority().equals(permission));
        }
        return false;
    }

    private class BindedValidationUserDetails extends UserDetails {

        /**
         *
         */
        private static final long serialVersionUID = -8378729763230637533L;

        public BindedValidationUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
            super(user, authorities);
        }

        /**
         * Check for service user details valid flags
         */
        public boolean isInvalid() {
            if(super.isInvalid() | invalidUserIds.remove(this.getUserId())) {
                return true;
            }
            long gt = invalidGroupIdsAndTimes.getOrDefault(this.getGroupId(), 0L);
            return gt >= this.getCreateTime();
        };
    }

}
