package ru.pixelmongo.pixelmongo.model.dao.primary;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ingame_news")
public class IngameNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    private String text;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "created", nullable = false)
    private Date createDate;

    @Column(name = "updated", nullable = false)
    private Date updateDate;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private IngameNewsChannel channel;

    public IngameNews() {}

    public IngameNews(IngameNewsChannel channel, String title, String text, User author) {
        this.title = title;
        this.text = text;
        this.authorName = author.getName();
        this.channel = channel;
        this.createDate = this.updateDate = new Date();
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public IngameNewsChannel getChannel() {
        return channel;
    }

    public void setChannel(IngameNewsChannel channel) {
        this.channel = channel;
    }

    public Date getCreateDate() {
        return createDate;
    }

}
