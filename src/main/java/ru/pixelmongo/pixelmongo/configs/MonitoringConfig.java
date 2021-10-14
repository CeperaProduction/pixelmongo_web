package ru.pixelmongo.pixelmongo.configs;

import java.time.Duration;

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

    @Value("${monitoring.connection_timeout}")
    private Duration timeout;

    @Value("${monitoring.next_ping_delay}")
    private Duration nextPingDelay;

    @Value("${monitoring.server_cache_time}")
    private Duration serverCacheUpdatePeriod;

    @Bean
    public MonitoringService monitoringService() {
        return new MonitoringServiceImpl(timeout.toMillis(), nextPingDelay.toMillis(),
                serverCacheUpdatePeriod.toMillis(), debug);
    }

}
