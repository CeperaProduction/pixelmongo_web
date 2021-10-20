package ru.pixelmongo.pixelmongo.model.dto.vk;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VKPost {

    private int id;

    private int date;

    private String text = "";

    private List<VKPost> reposts = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("copy_history")
    public List<VKPost> getReposts() {
        return reposts;
    }

    @JsonProperty("copy_history")
    public void setReposts(List<VKPost> reposts) {
        this.reposts = reposts;
    }



}
