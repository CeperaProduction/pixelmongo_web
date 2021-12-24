package ru.pixelmongo.pixelmongo.model.dto.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import ru.pixelmongo.pixelmongo.model.dao.primary.IngameNewsChannel;

public class IngameNewsChannelForm {

    @NotBlank(message = "{value.empty}")
    @Size(max = 255, message = "{value.too_long}")
    private String name = "";

    public IngameNewsChannelForm() {}

    public IngameNewsChannelForm(IngameNewsChannel channel) {
        this.name = channel.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
