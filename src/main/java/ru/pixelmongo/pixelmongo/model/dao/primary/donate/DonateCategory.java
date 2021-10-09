package ru.pixelmongo.pixelmongo.model.dao.primary.donate;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import ru.pixelmongo.pixelmongo.model.dao.OrderedData;

@Entity
@Table(name = "donate_categories")
public class DonateCategory implements OrderedData<Integer>{

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

    @Column(name="diplay", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private DonateDisplayType displayType = DonateDisplayType.DEFAULT;

    public DonateCategory() {}

    public DonateCategory(String title, DonatePage page) {
        this.title = title;
        this.page = page;
    }

    @Override
    public Integer getId() {
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

    @Override
    public int getOrdinary() {
        return ordinary;
    }

    @Override
    public void setOrdinary(int ordinary) {
        this.ordinary = ordinary;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public DonateDisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(DonateDisplayType displayType) {
        this.displayType = displayType;
    }

    public List<DonatePack> getPacks() {
        return packs;
    }

}
