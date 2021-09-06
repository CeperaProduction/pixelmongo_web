package ru.pixelmongo.pixelmongo.services;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ru.pixelmongo.pixelmongo.model.entities.MonitoringServer;

public interface MonitoringService {
    
    public static final Logger LOGGER = LogManager.getLogger("Monitoring");
    
    public long getLastUpdateTime();
    
    public Map<String, MonitoringResult> getMonitoringMap();
    
    public List<MonitoringResult> getMonitoringList();

    /**
     * Synchronous server status request
     * @param server
     * @return
     */
    public MonitoringResult pingServer(MonitoringServer server);
    
    public void markServersChanged();
    
    public static interface MonitoringResult {
        
        public boolean isOnline();
        
        @JsonProperty("tag")
        public String getServerTag();

        @JsonProperty("name")
        public String getServerName();

        @JsonProperty("description")
        public String getServerDescription();
        
        public int getCurrentPlayers();

        public int getMaxPlayers();

        public String getMotd();
        
        @JsonIgnore
        public int getPingTime();
        
        @JsonIgnore
        public int getPriority();
        
    }

}
