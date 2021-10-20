package ru.pixelmongo.pixelmongo.configs;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ru.pixelmongo.pixelmongo.services.VKService;
import ru.pixelmongo.pixelmongo.services.impl.VKServiceImpl;

@Configuration
@PropertySource("classpath:vk.properties")
public class VKConfig {

    @Value("${vk.api.url}")
    private String apiUrl;

    @Value("${vk.api.version}")
    private String apiVersion;

    @Value("${vk.api.key}")
    private String apiKey;

    @Value("#{${vk.api.groups}}")
    private Map<String, String> groups;

    @Value("${vk.api.news.update_period}")
    private Duration newsUpdatePeriod;

    @Value("${vk.api.news.max}")
    private int maxNews;

    @Bean
    public VKService vkService() {
        VKServiceImpl vk = new VKServiceImpl(apiUrl, apiKey, apiVersion);
        vk.setNewsUpdatePeriod(newsUpdatePeriod.toMillis());
        vk.setMaxNews(maxNews);
        vk.setGroups(groups);
        return vk;
    }

}
