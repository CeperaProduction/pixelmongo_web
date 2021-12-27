package ru.pixelmongo.pixelmongo.configs;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ru.pixelmongo.pixelmongo.services.PromocodeService;
import ru.pixelmongo.pixelmongo.services.impl.PromocodeServiceImpl;

@Configuration
@PropertySource("classpath:promocodes.properties")
public class PromocodeConfig {

    @Value("${promocodes.require_email_confirm}")
    private boolean requireEmailConfirm;

    @Value("${promocodes.failures.max}")
    private int failMaxCount;

    @Value("${promocodes.failures.time}")
    private Duration failBlockTime;

    @Bean
    public PromocodeService promocodeService() {
        return new PromocodeServiceImpl(requireEmailConfirm, failMaxCount, failBlockTime.toMillis());
    }

}
