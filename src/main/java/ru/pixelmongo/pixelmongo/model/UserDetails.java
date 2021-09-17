package ru.pixelmongo.pixelmongo.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

import org.springframework.security.core.GrantedAuthority;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;

public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {

    /**
     *
     */
    private static final long serialVersionUID = 5947676842275095406L;

    private final int userId;
    private final String name;

    private int groupId;
    private String password;
    private Collection<? extends GrantedAuthority> authorities = Collections.emptyList();

    private long createTime;
    private boolean invalid;

    public UserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.userId = user.getId();
        this.name = user.getName();
        this.password = user.getPassword();
        this.groupId = user.getGroup().getId();
        this.setAuthorities(authorities);
        this.createTime = System.currentTimeMillis();
    }

    public int getUserId() {
        return userId;
    }

    public int getGroupId() {
        return groupId;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public long getCreateTime() {
        return createTime;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void invalidate() {
        this.invalid = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    private void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        TreeSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new AuthorityComparator());
        for (GrantedAuthority grantedAuthority : authorities) {
            sortedAuthorities.add(grantedAuthority);
        }
        this.authorities = Collections.unmodifiableCollection(sortedAuthorities);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserDetails) {
            return this.name.equals(((UserDetails) obj).name);
        }
        return false;
    }

    /**
     * Returns the hashcode of the {@code username}.
     */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(" [");
        sb.append("ID=").append(this.userId).append(", ");
        sb.append("Name=").append(this.name).append(", ");
        sb.append("Password=[PROTECTED], ");
        sb.append("Authorities=").append(this.authorities).append("]");
        return sb.toString();
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority> {

        @Override
        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            if (g2.getAuthority() == null) {
                return -1;
            }
            if (g1.getAuthority() == null) {
                return 1;
            }
            return g1.getAuthority().compareTo(g2.getAuthority());
        }

    }

}
