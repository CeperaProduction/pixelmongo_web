package ru.pixelmongo.pixelmongo.services;

import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmation;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmationType;

public interface MailedConfirmationService {

    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * See {@link MailedConfirmationService#sendConfirmation(MailedConfirmationType, User, Locale, HttpServletRequest, Object...)}
     * @param <T>
     * @param type
     * @param user
     * @param loc
     * @param args
     * @return
     */
    public default <T extends MailedConfirmation> T sendConfirmation(MailedConfirmationType type,
            User user, Locale loc, Object... args){
        return sendConfirmation(type, user, loc, null, args);
    }

    /**
     * Make mailed confirmation, store it and send mail to user's email address.
     * @param <T>
     * @param type
     * @param user
     * @param loc user's locale
     * @param request if not null, than store last send time in session
     * @param args
     * @return generated confirmation object
     *
     * @throws IllegalArgumentException, MailedConfirmationHandlerNotFoundException
     */
    public <T extends MailedConfirmation> T sendConfirmation(MailedConfirmationType type,
            User user, Locale loc, HttpServletRequest request, Object... args);

    /**
     * Process mailed confirmation.
     * @param key
     * @param locale
     * @param request
     * @param response
     * @return url to redirect
     * @throws Exception
     */
    public String processConfirmation(String key, Locale locale, HttpServletRequest request,
            HttpServletResponse response) throws Exception;

    public <T extends MailedConfirmation> Optional<T> getActiveConfirmation(String key, MailedConfirmationType type);

    public void consumeConfirmation(MailedConfirmation confirmation);

    public default void consumeConfirmations(User user, MailedConfirmationType type) {
        consumeConfirmations(user.getId(), type);
    }

    public void consumeConfirmations(int userId, MailedConfirmationType type);

    /**
     * Find last active mailed confirmation for given user and type
     * @param <T>
     * @param userId
     * @param type
     * @return
     */
    public <T extends MailedConfirmation> Optional<T> getLastConfirmation(int userId, MailedConfirmationType type);

    /**
     * see {@link MailedConfirmationService#getLastConfirmation(int, MailedConfirmationType)}
     * @param <T>
     * @param user
     * @param type
     * @return
     */
    public default <T extends MailedConfirmation> Optional<T> getLastConfirmation(User user, MailedConfirmationType type){
        return getLastConfirmation(user.getId(), type);
    }

    /**
     * see {@link MailedConfirmationService#checkForSpam(int, MailedConfirmationType, HttpServletRequest)}
     * @param user
     * @param type
     * @return
     */
    public default long checkForSpam(User user, MailedConfirmationType type) {
        return checkForSpam(user.getId(), type, null);
    }

    /**
     * see {@link MailedConfirmationService#checkForSpam(int, MailedConfirmationType, HttpServletRequest)}
     * @param userId
     * @param type
     * @return
     */
    public default long checkForSpam(int userId, MailedConfirmationType type) {
        return checkForSpam(0, type, null);
    }

    /**
     * see {@link MailedConfirmationService#checkForSpam(int, MailedConfirmationType, HttpServletRequest)}
     * @param type
     * @param request
     * @return
     */
    public default long checkForSpam(MailedConfirmationType type, HttpServletRequest request) {
        return checkForSpam(0, type, request);
    }

    /**
     * see {@link MailedConfirmationService#checkForSpam(int, MailedConfirmationType, HttpServletRequest)}
     * @param user
     * @param type
     * @param request
     * @return
     */
    public default long checkForSpam(User user, MailedConfirmationType type, HttpServletRequest request) {
        return checkForSpam(user.getId(), type, request);
    }

    /**
     * Search for last requested mailed confirmation and return time in milliseconds until
     * next confirmation of given type will be available;
     *
     * @param userId 0 - don't check user
     * @param type
     * @param request null - don't check session
     * @return
     */
    public long checkForSpam(int userId, MailedConfirmationType type, HttpServletRequest request);

    public String printAwaitTime(long awaitTimeMs);

}
