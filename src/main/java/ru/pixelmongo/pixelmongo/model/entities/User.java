package ru.pixelmongo.pixelmongo.model.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 32, nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String email;

    //password hash
    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private UserGroup group;

    @Column(nullable = false)
    private int balance = 0;

    @Column(name="reg_date", nullable = false)
    private Date registrationDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @OrderBy("date DESC")
    private List<UserLoginRecord> loginRecords;

    public User() {}

    public User(String name, UserGroup group, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.password = passwordHash;
        this.group = group;
        this.registrationDate = new Date();
        this.loginRecords = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserGroup getGroup() {
        return group;
    }

    public void setGroup(UserGroup group) {
        this.group = group;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwordHash) {
        this.password = passwordHash;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<UserLoginRecord> getLoginRecords() {
        return loginRecords;
    }

}
