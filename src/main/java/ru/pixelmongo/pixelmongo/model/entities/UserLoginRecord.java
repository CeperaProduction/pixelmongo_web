package ru.pixelmongo.pixelmongo.model.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "users_login_data")
public class UserLoginRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(length = 16, nullable = false)
    private String ip;
    
    @Column(nullable = false)
    private Date date = new Date();
    
    public UserLoginRecord() {}
    
    public UserLoginRecord(String ip) {
        this.ip = ip;
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
    
}
