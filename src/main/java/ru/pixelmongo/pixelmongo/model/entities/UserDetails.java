package ru.pixelmongo.pixelmongo.model.entities;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class UserDetails extends org.springframework.security.core.userdetails.User {

    /**
     *
     */
    private static final long serialVersionUID = 5947676842275095406L;

    private final int userId;

    public UserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getName(), user.getPassword(), authorities);
        this.userId = user.getId();
    }

    public int getUserId() {
        return userId;
    }

}
