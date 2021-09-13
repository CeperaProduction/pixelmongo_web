package ru.pixelmongo.pixelmongo.model.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;

@Entity(name = "rules")
public class Rules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "admin.rules.title.empty")
    private String title = "";

    @Lob
    private String content = "";

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void copyTo(Rules other) {
        other.setTitle(getTitle());
        other.setContent(getContent());
    }

}
