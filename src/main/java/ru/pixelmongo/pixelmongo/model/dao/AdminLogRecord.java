package ru.pixelmongo.pixelmongo.model.dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "admin_logs")
public class AdminLogRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private String data;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_ip", nullable = false)
    private String userIp;

    public AdminLogRecord() {}

    public AdminLogRecord(User user, String userIp, String data) {
        this.userId = user.getId();
        this.userName = user.getName();
        this.userIp = userIp;
        this.data = data;
        this.date = new Date();
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getData() {
        return data;
    }

    /**
     * No refference to User to keep record exist if User is no longer exists
     */
    public int getUserId() {
        return userId;
    }

    /**
     * No refference to User to keep record exist if User is no longer exists
     */
    public String getUserName() {
        return userName;
    }

    public String getUserIp() {
        return userIp;
    }

}
