package ru.pixelmongo.pixelmongo.handlers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmation;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmationType;

public interface MailedConfirmationHandler<T extends MailedConfirmation> {

    public MailedConfirmationType getType();

    /**
     * Make mailed confirmation instance
     * @param key
     * @param user
     * @param args
     * @return
     * @throws IllegalArgumentException if args are not valid
     */
    public T makeConfirmation(String key, User user, Object... args) throws IllegalArgumentException;

    /**
     * Process mailed confirmation. Executes when user comes from link in mail.
     * @param confirmation
     * @param loc
     * @param request
     * @param response
     * @return url to redirect
     * @throws Exception
     */
    public String processConfirmation(T confirmation, Locale loc, HttpServletRequest request,
            HttpServletResponse response) throws Exception;

    /**
     * Mail template name.
     * Current {@link MailedConfirmation} object will be provided as attribute <b>confirm</b>
     * Mail title will be provided as attribute <b>title</b>
     * Confirmation link will be provided as attribute <b>link</b>
     * Current user object will be provided as attribute <b>user</b>
     * Site absolute url will be provided as attribute <b>baseUrl</b>
     * Template name is not absolute. 'mail/' will be prepended automatically.
     * @return
     */
    public String getMailTemplate();

    public String getMailTitle(Locale loc);

    public default ConsumeAction consumeAfterProcess() {
        return ConsumeAction.CURRENT;
    }

    public default ConsumeAction consumeOnException() {
        return ConsumeAction.CURRENT;
    }

    public enum ConsumeAction {
        NONE, CURRENT, ALL;
    }

}
