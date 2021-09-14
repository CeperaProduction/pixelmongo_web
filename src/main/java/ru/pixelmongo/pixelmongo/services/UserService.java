package ru.pixelmongo.pixelmongo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import ru.pixelmongo.pixelmongo.exceptions.UserAlreadyExistsException;
import ru.pixelmongo.pixelmongo.model.UserDetails;
import ru.pixelmongo.pixelmongo.model.dao.User;
import ru.pixelmongo.pixelmongo.model.dao.UserLoginRecord;

public interface UserService extends UserDetailsService{

    public Optional<User> getUser(UserDetails userDetails);

    public List<GrantedAuthority> makeAuthority(User user);

    public void updateAuthorities(User user, Object principal);

    public default void updateAuthorities(User user) {
        updateAuthorities(user, null);
    }

    public void saveLoginData(User user, String ip);

    public User registerUser(String name, String email, String password,
            String registerIp) throws UserAlreadyExistsException;

    public void changePassword(User user, Object principal, String newPassword, boolean saveUser);

    /**
     * Change password but don't save user in repositories
     * @param user
     * @param newPassword
     */
    public default void changePassword(User user, Object principal, String newPassword) {
        changePassword(user, principal, newPassword, false);
    }

    /**
     * Change password but don't save user in repositories. Uses current user principal.
     * @param user
     * @param newPassword
     */
    public default void changePassword(User user, String newPassword) {
        changePassword(user, null, newPassword, false);
    }

    public Optional<UserLoginRecord> getLastLogin(User user);

    /**
     * Check if current user has given permission
     * @param permission
     * @return
     */
    public boolean hasPerm(String permission);

    /**
     * Check if given user has given permission
     * @param permission
     * @return
     */
    public boolean hasPerm(User user, String permission);

}
