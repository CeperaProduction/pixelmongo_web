package ru.pixelmongo.pixelmongo.model.dto.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import ru.pixelmongo.pixelmongo.model.dao.MonitoringServer;
import ru.pixelmongo.pixelmongo.utils.DefaulPatterns;

public class MonitoringServerForm {

    private int id;

    @Pattern(regexp = DefaulPatterns.SIMPLE_TAG_PATTERN, message = "{admin.tag.invalid}")
    private String tag;

    @NotBlank
    private String name;

    @NotNull
    private String description = "";

    @Pattern(regexp = DefaulPatterns.IPV4_PATTERN, message = "{admin.ip.invalid}")
    private String ip;

    @NotNull
    private short port = 25565;

    private boolean enabled = true;

    private boolean online;

    public MonitoringServerForm() {}

    public MonitoringServerForm(MonitoringServer server, boolean online) {
        id = server.getId();
        tag = server.getTag();
        name = server.getName();
        description = server.getDescription();
        ip = server.getIp();
        port = server.getPort();
        enabled = server.isEnabled();
        this.online = online;
    }

    public void apply(MonitoringServer server) {
        server.setTag(tag);
        server.setName(name);
        server.setDescription(description);
        server.setIp(ip);
        server.setPort(port);
        server.setEnabled(enabled);
    }

    public int getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public short getPort() {
        return port;
    }

    public void setPort(short port) {
        this.port = port;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isOnline() {
        return online;
    }

}
