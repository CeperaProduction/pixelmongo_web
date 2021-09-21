package ru.pixelmongo.pixelmongo.model.dto.forms.donate;

import javax.validation.constraints.NotBlank;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateServer;

public class DonateServerForm {

    @NotBlank(message = "{value.empty}")
    private String displayName;

    @NotBlank(message = "{value.empty}")
    private String configName;

    private String key = "";

    public DonateServerForm() {}

    public DonateServerForm(DonateServer server) {
        this.displayName = server.getDisplayName();
        this.configName = server.getConfigName();
        this.key = server.getKey();
    }

    public void apply(DonateServer server) {
        server.setDisplayName(this.displayName);
        server.setConfigName(this.configName);
        server.setKey(this.key);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
