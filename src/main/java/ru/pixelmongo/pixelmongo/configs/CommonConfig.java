package ru.pixelmongo.pixelmongo.configs;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create()
                    .build());
        RestTemplate rest = new RestTemplate(clientHttpRequestFactory);
        /*
        rest.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

            }
        });
        */
        return rest;
    }

    @Bean
    public DateFormatter dateFormatter() {
        return new DateFormatter("yyyy-MM-dd HH:mm:ss");
    }

}
