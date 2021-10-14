package ru.pixelmongo.pixelmongo.model.dao.primary.confirm;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;

@Entity
@DiscriminatorValue(MailedConfirmationType.Values.CHANGE_PASSWORD)
public class MailedConfirmationPasswordChange extends MailedConfirmation{

    public MailedConfirmationPasswordChange() {}

    public MailedConfirmationPasswordChange(String key, User user) {
        super(key, user);
    }

}
