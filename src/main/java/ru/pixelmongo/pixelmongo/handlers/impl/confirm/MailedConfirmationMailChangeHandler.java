package ru.pixelmongo.pixelmongo.handlers.impl.confirm;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.MailedConfirmationHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmationMailChange;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmationType;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;

@Component
public class MailedConfirmationMailChangeHandler implements MailedConfirmationHandler<MailedConfirmationMailChange>{

    @Autowired
    private UserRepository users;

    @Autowired
    private PopupMessageService popup;

    @Autowired
    private MessageSource msg;

    @Override
    public MailedConfirmationType getType() {
        return MailedConfirmationType.MAIL_CHANGE;
    }

    @Override
    public MailedConfirmationMailChange makeConfirmation(String key, User user, Object... args) throws IllegalArgumentException {
        if(args.length == 0 || !(args[0] instanceof String))
            throw new IllegalArgumentException("First argument must be email string");
        String email = (String) args[0];
        return new MailedConfirmationMailChange(key, user, email);
    }

    @Override
    public String getMailTemplate() {
        return "mail_change";
    }

    @Override
    public String getMailTitle(Locale loc) {
        return msg.getMessage("mconfirm.mailchange.title", null, loc);
    }

    @Override
    public String processConfirmation(MailedConfirmationMailChange confirmation, Locale loc,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = users.findById(confirmation.getUserId())
                .orElseThrow(()->new IllegalStateException(msg.getMessage("error.status.404.user", null, loc)));
        user.setEmail(confirmation.getEmail());
        user.setEmailConfirmed(false);
        users.save(user);
        popup.sendUsingCookies(new PopupMessage(msg.getMessage("mconfirm.mailchange.done", null, loc),
                PopupMessage.Type.INFO), request, response);
        return "/profile";
    }

    @Override
    public ConsumeAction consumeAfterProcess() {
        return ConsumeAction.ALL;
    }

}
