package ru.pixelmongo.pixelmongo.handlers.impl.confirm;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import ru.pixelmongo.pixelmongo.handlers.MailedConfirmationHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmationPasswordChange;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmationType;

@Component
public class MailedConfirmationPasswordChangeHandler implements MailedConfirmationHandler<MailedConfirmationPasswordChange>{

    @Autowired
    private MessageSource msg;

    @Override
    public MailedConfirmationType getType() {
        return MailedConfirmationType.CHANGE_PASSWORD;
    }

    @Override
    public MailedConfirmationPasswordChange makeConfirmation(String key, User user, Object... args)
            throws IllegalArgumentException {
        return new MailedConfirmationPasswordChange(key, user);
    }

    @Override
    public String getMailTitle(Locale loc) {
        return msg.getMessage("mconfirm.passwordchange.title", null, loc);
    }

    @Override
    public String getMailTemplate() {
        return "password_change";
    }

    @Override
    public String processConfirmation(MailedConfirmationPasswordChange confirmation, Locale loc,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/password");
        builder.queryParam("key", confirmation.getKey());
        return builder.toUriString();
    }

    @Override
    public ConsumeAction consumeAfterProcess() {
        return ConsumeAction.NONE;
    }

}
