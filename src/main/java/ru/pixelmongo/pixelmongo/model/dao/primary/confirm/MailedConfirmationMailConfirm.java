package ru.pixelmongo.pixelmongo.model.dao.primary.confirm;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;

@Entity
@DiscriminatorValue(MailedConfirmationType.Values.MAIL_CONFIRM)
public class MailedConfirmationMailConfirm extends MailedConfirmation{

    @Column(name = "data")
    private String email;

    public MailedConfirmationMailConfirm() {}

    public MailedConfirmationMailConfirm(String key, User user, String email) {
        super(key, user);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

}
