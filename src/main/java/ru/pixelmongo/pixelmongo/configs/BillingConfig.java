package ru.pixelmongo.pixelmongo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ru.pixelmongo.pixelmongo.services.impl.BillingServiceImpl;

import ru.pixelmongo.pixelmongo.services.BillingService;

@Configuration
@PropertySource("classpath:billing.properties")
public class BillingConfig {

    @Bean
    public BillingService billingService() {
        return new BillingServiceImpl();
    }

}
