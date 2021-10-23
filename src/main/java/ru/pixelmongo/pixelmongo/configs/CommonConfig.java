package ru.pixelmongo.pixelmongo.configs;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CommonConfig {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(defaultTimeZone());
    }

    @Bean
    public TimeZone defaultTimeZone() {
        return TimeZone.getTimeZone("GMT+3:00");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DateFormatter dateFormatter() {
        return new DateFormatter("yyyy-MM-dd HH:mm:ss");
    }

}
