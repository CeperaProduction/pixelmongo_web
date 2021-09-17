package ru.pixelmongo.pixelmongo.model.dao.primary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.pixelmongo.pixelmongo.services.MonitoringService;

@Entity
@Table(name = "monitoring_servers")
public class MonitoringServer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "[a-zA-Z0-9_-]+", message = "{admin.monitoring.tag.invalid}")
    private String tag;

    @Column(nullable = false)
    private String name;

    private String description = "";

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    private short port = 25565;

    @Column(nullable = false)
    private int ordinary = 20;

    private boolean enabled = true;

    private transient boolean nowPinging = false;

    public MonitoringServer() {}

    public MonitoringServer(String tag, String name, String ip, short port) {
        this.tag = tag;
        this.name = name;
        this.ip = ip;
        this.port = port;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public short getPort() {
        return port;
    }

    public void setPort(short port) {
        this.port = port;
    }

    public int getOrdinary() {
        return ordinary;
    }

    public void setOrdinary(int priority) {
        this.ordinary = priority;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isNowPinging() {
        return nowPinging;
    }

    public void setNowPinging(boolean nowPinging) {
        this.nowPinging = nowPinging;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            MonitoringService.LOGGER.catching(e);
            return super.toString();
        }
    }

}
