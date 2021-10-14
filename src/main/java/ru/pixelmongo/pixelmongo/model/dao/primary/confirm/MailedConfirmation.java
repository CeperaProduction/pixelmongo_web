package ru.pixelmongo.pixelmongo.model.dao.primary.confirm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;

@Entity
@Table(name = "mailed_confirms")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 31)
public class MailedConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "confirm_key", length = 63, unique = true, nullable = false)
    private String key;

    @Column(name = "type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private MailedConfirmationType type;

    @Column(name = "created")
    private Date createDate;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "user_name")
    private String userName;

    public MailedConfirmation() {}

    public MailedConfirmation(String key, User user) {
        this.key = key;
        this.userId = user.getId();
        this.userName = user.getName();
        this.createDate = new Date();
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public MailedConfirmationType getType() {
        return type;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Date getCreateDate() {
        return createDate;
    }

}
