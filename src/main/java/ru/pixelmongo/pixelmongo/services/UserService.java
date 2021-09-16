package ru.pixelmongo.pixelmongo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.pixelmongo.pixelmongo.exceptions.UserAlreadyExistsException;
import ru.pixelmongo.pixelmongo.model.AnonymousUser;
import ru.pixelmongo.pixelmongo.model.UserDetails;
import ru.pixelmongo.pixelmongo.model.dao.User;
import ru.pixelmongo.pixelmongo.model.dao.UserGroup;
import ru.pixelmongo.pixelmongo.model.dao.UserLoginRecord;

public interface UserService extends UserDetailsService{

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * Returns current request user. If no user exists, then return {@link AnonymousUser}
     */
    public User getCurrentUser();

    /**
     * Find user using information in details
     * @param userDetails
     * @return
     */
    public Optional<User> getUser(UserDetails userDetails);

    public List<GrantedAuthority> makeAuthority(User user);

    /**
     * Store information about this login operation
     * @param user
     * @param ip
     */
    public void saveLoginData(User user, String ip);

    /**
     * Register user. Password will be encoded using current {@link PasswordEncoder}
     * @param user
     * @param newPassword
     * @param saveUser
     */
    public User registerUser(String name, String email, String password,
            String registerIp) throws UserAlreadyExistsException;

    /**
     * Changes user password encoding it using current {@link PasswordEncoder}
     * @param user
     * @param newPassword
     * @param saveUser
     */
    public void changePassword(User user, String newPassword, boolean saveUser);

    /**
     * Force update cached {@link UserDetails} on next user request.
     * @param user
     */
    public void invalidateDetails(User user);

    /**
     * Force update cached {@link UserDetails} on next user request.
     * Applies to all members of given group.
     * @param group
     */
    public void invalidateDetails(UserGroup group);

    /**
     * Change password but don't save user in repositories
     * @param user
     * @param newPassword
     */
    public default void changePassword(User user, String newPassword) {
        changePassword(user, newPassword, false);
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
