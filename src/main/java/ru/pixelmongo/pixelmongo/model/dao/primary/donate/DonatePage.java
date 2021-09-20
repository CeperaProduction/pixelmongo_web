package ru.pixelmongo.pixelmongo.model.dao.primary.donate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import ru.pixelmongo.pixelmongo.model.dao.OrderedData;

@Entity
@Table(name = "donate_pages")
public class DonatePage implements OrderedData<Integer>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String tag;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "page", cascade = CascadeType.REMOVE)
    @OrderBy("ordinary")
    private List<DonateCategory> categories;

    private int ordinary = 0;

    private boolean hidden = false;

    private boolean enabled = true;

    @ManyToMany
    @JoinTable(name = "donate_pages_servers",
            joinColumns = @JoinColumn(name="page_id"),
            inverseJoinColumns = @JoinColumn(name="server_id"))
    private List<DonateServer> servers = new ArrayList<>();

    public DonatePage() {}

    public DonatePage(String tag, String title) {
        this.tag = tag;
        this.title = title;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public List<DonateServer> getServers() {
        return servers;
    }

}
