package ru.pixelmongo.pixelmongo.model.dao.primary.donate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "donate_servers")
public class DonateServer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "config_name", unique = true, nullable = false)
    private String configName;

    private String key = "";

    public DonateServer() {}

    public DonateServer(String displayName, String configName) {
        this.displayName = displayName;
        this.configName = configName;
    }

    public int getId() {
        return id;
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
