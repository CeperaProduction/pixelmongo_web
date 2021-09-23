package ru.pixelmongo.pixelmongo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource("classpath:config.properties")
public class CommonConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DateFormatter dateFormatter() {
        return new DateFormatter("yyyy-MM-dd HH:mm:ss");
    }

}
