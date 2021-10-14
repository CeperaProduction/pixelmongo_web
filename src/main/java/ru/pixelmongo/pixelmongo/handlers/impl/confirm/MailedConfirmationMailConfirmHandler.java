package ru.pixelmongo.pixelmongo.handlers.impl.confirm;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.MailedConfirmationHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmationMailConfirm;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmationType;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;

@Component
public class MailedConfirmationMailConfirmHandler implements MailedConfirmationHandler<MailedConfirmationMailConfirm>{

    @Autowired
    private UserRepository users;

    @Autowired
    private PopupMessageService popup;

    @Autowired
    private MessageSource msg;

    @Override
    public MailedConfirmationType getType() {
        return MailedConfirmationType.MAIL_CONFIRM;
    }

    @Override
    public MailedConfirmationMailConfirm makeConfirmation(String key, User user, Object... args) throws IllegalArgumentException {
        if(args.length == 0 || !(args[0] instanceof String))
            throw new IllegalArgumentException("First argument must be email string");
        String email = (String) args[0];
        return new MailedConfirmationMailConfirm(key, user, email);
    }

    @Override
    public String getMailTitle(Locale loc) {
        return msg.getMessage("mconfirm.mailconfirm.title", null, loc);
    }

    @Override
    public String getMailTemplate() {
        return "mail_confirm";
    }

    @Override
    public String processConfirmation(MailedConfirmationMailConfirm confirmation, Locale loc,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = users.findById(confirmation.getUserId())
                .orElseThrow(()->new IllegalStateException(msg.getMessage("error.status.404.user", null, loc)));
        if(!user.getEmail().equals(confirmation.getEmail()))
            throw new IllegalStateException(msg.getMessage("mconfirm.mailconfirm.not_equal", null, loc));
        user.setEmailConfirmed(true);
        users.save(user);
        popup.sendUsingCookies(new PopupMessage(msg.getMessage("mconfirm.mailconfirm.done", null, loc),
                PopupMessage.Type.OK), request, response);
        return "/profile";
    }

    @Override
    public ConsumeAction consumeAfterProcess() {
        return ConsumeAction.ALL;
    }

}
