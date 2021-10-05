package ru.pixelmongo.pixelmongo.model.dao.primary.donate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;

@Entity
@Table(name = "donate_extra_logs")
public class DonateExtraRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user", nullable = false)
    private String userName;

    @Column(name = "money_spent")
    private int spentMoney;

    private Date date;

    private String data;

    public DonateExtraRecord() {}

    public DonateExtraRecord(User user, int spentMoney, String data) {
        this.userName = user.getName();
        this.spentMoney = spentMoney;
        this.data = data;
        this.date = new Date();
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSpentMoney() {
        return spentMoney;
    }

    public void setSpentMoney(int spentMoney) {
        this.spentMoney = spentMoney;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
