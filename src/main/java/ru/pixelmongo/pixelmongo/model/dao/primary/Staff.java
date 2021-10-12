package ru.pixelmongo.pixelmongo.model.dao.primary;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import ru.pixelmongo.pixelmongo.model.dao.OrderedData;

@Entity
@Table(name = "staff")
public class Staff implements OrderedData<Integer>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", unique = true, nullable = false, updatable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    private int ordinary;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "foreground", column = @Column(name = "display_front")),
        @AttributeOverride(name = "background", column = @Column(name = "display_back")),
    })
    private StaffDisplay display = new StaffDisplay();

    public Staff() {}

    public Staff(User user, String title) {
        this.user = user;
        this.title = title;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getOrdinary() {
        return ordinary;
    }

    public void setOrdinary(int ordinary) {
        this.ordinary = ordinary;
    }

    public StaffDisplay getDisplay() {
        return display;
    }

    public void setDisplay(StaffDisplay display) {
        this.display = display;
    }





}
