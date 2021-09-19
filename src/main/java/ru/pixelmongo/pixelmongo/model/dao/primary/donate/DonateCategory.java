package ru.pixelmongo.pixelmongo.model.dao.primary.donate;

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
import javax.persistence.Table;

@Entity
@Table(name = "donate_categories")
public class DonateCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "page_id", nullable = false)
    private DonatePage page;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    @OrderBy("ordinary")
    private List<DonatePack> packs;

    private boolean enabled = true;

    private int ordinary = 0;

    public DonateCategory() {}

    public DonateCategory(String title, DonatePage page) {
        this.title = title;
        this.page = page;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DonatePage getPage() {
        return page;
    }

    public void setPage(DonatePage page) {
        this.page = page;
    }

    public int getOrdinary() {
        return ordinary;
    }

    public void setOrdinary(int ordinary) {
        this.ordinary = ordinary;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
