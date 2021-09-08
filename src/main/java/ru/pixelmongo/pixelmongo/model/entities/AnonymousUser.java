package ru.pixelmongo.pixelmongo.model.entities;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

public final class AnonymousUser extends User{

    private static UserGroup group;
    private static Date regDate;
    private static AnonymousUser instance;

    public static AnonymousUser getInstance() {
        if(instance == null) {
            group = new AnonymousUserGroup();
            regDate = new Date(0);
            instance = new AnonymousUser();
        }
        return instance;
    }

    private AnonymousUser() {}

    @Override
    public String getName() {
        return "Anonymous";
    }

    @Override
    public String getEmail() {
       return "";
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public UserGroup getGroup() {
        return group;
    }

    @Override
    public Date getRegistrationDate() {
        return regDate;
    }

    @Override
    public List<UserLoginRecord> getLoginRecords() {
        return Collections.emptyList();
    }

    @Override
    public void setBalance(int balance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEmail(String email) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGroup(UserGroup group) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPassword(String passwordHash) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRegistrationDate(Date registrationDate) {
        throw new UnsupportedOperationException();
    }

    public static final class AnonymousUserGroup extends UserGroup{

        private AnonymousUserGroup() {}

        @Override
        public String getName() {
            return "Anonymous";
        }

        @Override
        public Set<User> getUsers() {
            return Collections.emptySet();
        }

        @Override
        public Set<UserPermission> getPermissions() {
            return Collections.emptySet();
        }

        @Override
        public void setName(String name) {
            throw new UnsupportedOperationException();
        }

    }

}
