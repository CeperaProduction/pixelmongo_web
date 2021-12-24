package ru.pixelmongo.pixelmongo.model.dto.forms;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ru.pixelmongo.pixelmongo.model.dao.primary.IngameNews;

public class IngameNewsForm {

    @NotBlank(message = "{value.empty}")
    @Size(max = 255, message = "{value.too_long}")
    private String title = "";

    @NotBlank(message = "{value.empty}")
    private String text = "";

    @NotNull(message = "{value.denied}")
    @Min(value = 1, message = "{value.denied}")
    private int channel;

    public IngameNewsForm() {}

    public IngameNewsForm(IngameNews news) {
        this.title = news.getTitle();
        this.text = news.getText();
        this.channel = news.getChannel().getId();
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public int getChannel() {
        return channel;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

}
