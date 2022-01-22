package ru.pixelmongo.pixelmongo.model.dao.primary;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "promocodes_activations")
public class PromocodeActivation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "promocode_id", nullable = false)
    private Promocode promocode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Date date;

    public PromocodeActivation() {}

    public PromocodeActivation(Promocode promocode, User user) {
        this.promocode = promocode;
        this.user = user;
        this.date = new Date();
    }

    public int getId() {
        return id;
    }

    public Promocode getPromocode() {
        return promocode;
    }

    public User getUser() {
        return user;
    }

    public Date getDate() {
        return date;
    }

}
