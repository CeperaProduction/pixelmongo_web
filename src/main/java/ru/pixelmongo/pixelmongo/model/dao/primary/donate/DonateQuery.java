package ru.pixelmongo.pixelmongo.model.dao.primary.donate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import ru.pixelmongo.pixelmongo.utils.StringListConverter;

@Entity
@Table(name = "donate_query")
public class DonateQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(name="cmds", nullable = false)
    @Lob
    @Convert(converter = StringListConverter.class)
    private List<String> commands = new ArrayList<>();

    @Column(nullable = false)
    private int date;

    @Column(nullable = false)
    private int dateCompleted = 0;

    @Column(nullable = false)
    private int executeAfter = 0;

    @Column(nullable = false)
    private String server;

    @Column(nullable = false)
    private String player;

    @Column(nullable = false)
    private boolean done = false;

    @Column(nullable = false)
    private boolean offline = false;

    @Column(nullable = false)
    private int invSpace = 0;

    /*
     * Yeah, thats right. This should be named so for other systems capability
     */
    @Column(name="isHiden", nullable = false)
    private boolean hidden;

    @Column(name="money_spent", nullable = false)
    private int spentMoney = 0;

    @Column(name="bonus_spent", nullable = false)
    private int spentBonus = 0;

    /*
     * No refference to keep exist when pack is already not.
     * Also this field can be used by other systems to mark special operations.
     */
    @Column(name="pack_id", nullable = false)
    private int packId = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "back_of")
    private DonateQuery backOf;

    @OneToOne(mappedBy = "backOf", fetch = FetchType.LAZY)
    private DonateQuery back;

    public DonateQuery() {}

    public DonateQuery(String title, String server, String player) {
        this.title = title;
        this.server = server;
        this.player = player;
        this.date = (int) (System.currentTimeMillis()/1000L);
        this.executeAfter = this.date;
    }

    /**
     * Make duplication of this query.
     * Id, time points, status, query sequence relations (back queries) won't copy.
     * @return
     */
    public DonateQuery duplicate() {
        DonateQuery newQuery = new DonateQuery();
        newQuery.title = this.title;
        newQuery.player = this.player;
        newQuery.server = this.server;
        newQuery.date = (int) (System.currentTimeMillis()/1000L);
        newQuery.executeAfter = newQuery.date;
        newQuery.commands = new ArrayList<>(this.commands);
        newQuery.offline = this.offline;
        newQuery.hidden = this.hidden;
        newQuery.invSpace = this.invSpace;
        newQuery.packId = this.packId;
        newQuery.spentMoney = this.spentMoney;
        return newQuery;
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

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(int dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public int getExecuteAfter() {
        return executeAfter;
    }

    public void setExecuteAfter(int executeAfter) {
        this.executeAfter = executeAfter;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public int getInvSpace() {
        return invSpace;
    }

    public void setInvSpace(int invSpace) {
        this.invSpace = invSpace;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getSpentMoney() {
        return spentMoney;
    }

    public void setSpentMoney(int spentMoney) {
        this.spentMoney = spentMoney;
    }

    public int getSpentBonus() {
        return spentBonus;
    }

    public void setSpentBonus(int spentBonus) {
        this.spentBonus = spentBonus;
    }

    public int getPackId() {
        return packId;
    }

    public void setPackId(int pack_id) {
        this.packId = pack_id;
    }

    /**
     * @return Query this query is back of. Null if this query is not back query
     */
    public DonateQuery getBackOf() {
        return backOf;
    }

    public void setBackOf(DonateQuery backOf) {
        this.backOf = backOf;
    }

    /**
     * @return Back query of this query. Null if it has no back query
     */
    public DonateQuery getBack() {
        return back;
    }

    public void setBack(DonateQuery back) {
        this.back = back;
    }

    public boolean isBackQuery() {
        return this.backOf != null;
    }

    public boolean hasBackQuery() {
        return this.back != null;
    }

    public Date getDateDate() {
        return date == 0 ? null : new Date(1000L * date);
    }

    public Date getExecuteAfterDate() {
        return executeAfter == 0 ? null : new Date(1000L * executeAfter);
    }

    public Date getDateCompletedDate() {
        return dateCompleted == 0 ? null : new Date(1000L * dateCompleted);
    }

}
