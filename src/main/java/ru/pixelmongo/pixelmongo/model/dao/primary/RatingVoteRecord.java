package ru.pixelmongo.pixelmongo.model.dao.primary;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import ru.pixelmongo.pixelmongo.handlers.RatingHandler;

@Entity
@Table(name = "rating_votes")
public class RatingVoteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private String rating;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    private int reward;

    public RatingVoteRecord() {}

    public RatingVoteRecord(RatingHandler handler, User user, int reward) {
        this.rating = handler.getName();
        this.userId = user.getId();
        this.userName = user.getName();
        this.date = new Date();
        this.reward = reward;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getRating() {
        return rating;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getReward() {
        return reward;
    }


}
