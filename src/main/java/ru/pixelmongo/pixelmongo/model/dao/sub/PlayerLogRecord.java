package ru.pixelmongo.pixelmongo.model.dao.sub;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="logger")
public class PlayerLogRecord {

    @Id
    private int id;

    private String player;

    private String type;

    @Lob
    private String data;

    @Column(name = "time")
    private int timestamp;

    private String world;

    private int x;

    private int y;

    private int z;

    private String server;

    public int getId() {
        return id;
    }

    public String getPlayer() {
        return player;
    }

    public String getType() {
        return type;
    }

    public String getServer() {
        return server;
    }

    public String getData() {
        return data;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public Date getDate() {
        return new Date(1000L*timestamp);
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

}
