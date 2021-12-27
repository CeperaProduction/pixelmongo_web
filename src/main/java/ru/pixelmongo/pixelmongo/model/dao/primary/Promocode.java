package ru.pixelmongo.pixelmongo.model.dao.primary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.lang.Nullable;

@Entity
@Table(name = "promocodes")
public class Promocode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private int value;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "max_uses", columnDefinition = "int(11) not null default 1")
    private int maxUses;

    @Column(name="times_used", columnDefinition = "int(11) not null default 0")
    private int timesUsed;

    @OneToMany(mappedBy = "promocode", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PromocodeActivation> activations = new ArrayList<PromocodeActivation>();

    public Promocode() {}

    public Promocode(String code, String title) {
        this.code = code;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Nullable
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(@Nullable Date endDate) {
        this.endDate = endDate;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(int timesUsed) {
        this.timesUsed = timesUsed;
    }

    public List<PromocodeActivation> getActivations() {
        return activations;
    }

}
