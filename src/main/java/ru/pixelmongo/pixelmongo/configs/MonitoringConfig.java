package ru.pixelmongo.pixelmongo.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ru.pixelmongo.pixelmongo.services.MonitoringService;
import ru.pixelmongo.pixelmongo.services.impl.MonitoringServiceImpl;

@Configuration
@PropertySource("classpath:monitoring.properties")
public class MonitoringConfig {

    @Value("${monitoring.debug_mode}")
    private boolean debug;

    @Value("${monitoring.server_cache_time}")
    private int timeout;

    @Value("${monitoring.connection_timeout_ms}")
    private int nextPingDelay;

    @Value("${monitoring.server_cache_time}")
    private int serverCacheUpdatePeriod;

    @Bean
    public MonitoringService monitoringService() {
        return new MonitoringServiceImpl(timeout, nextPingDelay, serverCacheUpdatePeriod, debug);
    }

}
