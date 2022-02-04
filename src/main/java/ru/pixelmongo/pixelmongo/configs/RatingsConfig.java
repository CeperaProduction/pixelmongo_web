package ru.pixelmongo.pixelmongo.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ru.pixelmongo.pixelmongo.services.RatingService;
import ru.pixelmongo.pixelmongo.services.impl.RatingServiceImpl;

@Configuration
@PropertySource("classpath:ratings.properties")
public class RatingsConfig {

    @Value("${ratings.reward}")
    private int reward;

    @Value("${ratings.once_per_day}")
    private boolean oncePerDay;

    @Bean
    public RatingService ratingService() {
        return new RatingServiceImpl(reward, oncePerDay);
    }

}
