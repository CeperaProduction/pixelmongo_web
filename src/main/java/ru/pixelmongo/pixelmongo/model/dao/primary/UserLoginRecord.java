package ru.pixelmongo.pixelmongo.model.dao.primary;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "users_login_data")
public class UserLoginRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 16, nullable = false)
    private String ip;

    @Column(name = "source", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private LoginSource source;

    @Column(nullable = false)
    private Date date;

    public UserLoginRecord() {}

    public UserLoginRecord(User user, String ip) {
        this(user, LoginSource.SITE, ip);
    }

    public UserLoginRecord(User user, LoginSource source, String ip) {
        this.user = user;
        this.ip = ip;
        this.date = new Date();
        this.source = source;
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public Date getDate() {
        return date;
    }

    public LoginSource getSource() {
        return source;
    }

    public enum LoginSource{
        SITE, LAUNCHER;
    }

}
