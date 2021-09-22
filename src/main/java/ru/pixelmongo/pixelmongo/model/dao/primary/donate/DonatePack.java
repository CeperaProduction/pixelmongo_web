package ru.pixelmongo.pixelmongo.model.dao.primary.donate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import ru.pixelmongo.pixelmongo.model.dao.OrderedData;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackToken;
import ru.pixelmongo.pixelmongo.utils.StringListConverter;

@Entity
@Table(name = "donate_packs")
public class DonatePack implements OrderedData<Integer>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Lob
    private String content = "";

    @Convert(converter = StringListConverter.class)
    private List<String> commands = new ArrayList<>();

    private boolean countable = false;

    private int cost = 100;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private DonateCategory category;

    private boolean timed = false;

    @Column(name = "timed_time")
    private int existTime = 0;

    @Column(name = "timed_commands")
    @Convert(converter = StringListConverter.class)
    private List<String> backCommands = new ArrayList<>();

    private int ordinary = 0;

    @Column(name = "hidden_give")
    private boolean hiddenGive = false;

    private boolean enabled = true;

    @Column(name = "inv_space")
    private int invSpace = 0;

    @Column(name = "offline")
    private boolean giveOffline = false;

    private byte discount = 0;

    @ManyToMany
    @JoinTable(name = "donate_packs_servers",
            joinColumns = @JoinColumn(name="pack_id"),
            inverseJoinColumns = @JoinColumn(name="server_id"))
    private List<DonateServer> servers = new ArrayList<>();

    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DonatePackToken> tokens = new ArrayList<>();

    public DonatePack() {}

    public DonatePack(String title, DonateCategory category) {
        this.title = title;
        this.category = category;
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

    public DonateCategory getCategory() {
        return category;
    }

    public void setCategory(DonateCategory category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    @Override
    public int getOrdinary() {
        return ordinary;
    }

    @Override
    public void setOrdinary(int ordinary) {
        this.ordinary = ordinary;
    }

    public boolean isCountable() {
        return countable;
    }

    public void setCountable(boolean countable) {
        this.countable = countable;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getInvSpace() {
        return invSpace;
    }

    public void setInvSpace(int invSpace) {
        this.invSpace = invSpace;
    }

    public boolean isGiveOffline() {
        return giveOffline;
    }

    public void setGiveOffline(boolean giveOffline) {
        this.giveOffline = giveOffline;
    }

    public boolean isHiddenGive() {
        return hiddenGive;
    }

    public void setHiddenGive(boolean hiddenGive) {
        this.hiddenGive = hiddenGive;
    }

    public boolean isTimed() {
        return timed;
    }

    public void setTimed(boolean timed) {
        this.timed = timed;
    }

    public int getExistTime() {
        return existTime;
    }

    public void setExistTime(int existTime) {
        this.existTime = existTime;
    }

    public List<String> getBackCommands() {
        return backCommands;
    }

    public void setBackCommands(List<String> backCommands) {
        this.backCommands = backCommands;
    }

    public List<DonateServer> getServers() {
        return servers;
    }

    public List<DonatePackToken> getTokens() {
        return tokens;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = (byte) (Math.max(Math.min(discount, 100), 0) / 100f);
    }

}
