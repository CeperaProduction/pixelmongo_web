package ru.pixelmongo.pixelmongo.model.dao.sub;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="banlist")
public class PlayerBanRecord {

    @Id
    private int id;

    @Column(name = "name", length = 31, nullable = false)
    private String player;

    @Column(name = "admin", length = 31, nullable = false)
    private String admin;

    private String reason = "";

    private int time;

    @Column(name = "tempTime")
    private int endTime = 0;

    public PlayerBanRecord() {}

    public int getId() {
        return id;
    }

    public String getPlayer() {
        return player;
    }

    public String getAdmin() {
        return admin;
    }

    public String getReason() {
        return reason;
    }

    public int getTime() {
        return time;
    }

    public int getEndTime() {
        return endTime;
    }

    public Date getDate() {
        return new Date(1000L * time);
    }

    public Date getEndDate() {
        return endTime > 0 ? new Date(1000L * endTime) : null;
    }



}
