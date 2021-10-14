package ru.pixelmongo.pixelmongo.configs;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ru.pixelmongo.pixelmongo.services.MailService;
import ru.pixelmongo.pixelmongo.services.MailedConfirmationService;
import ru.pixelmongo.pixelmongo.services.impl.MailServiceImpl;
import ru.pixelmongo.pixelmongo.services.impl.MailedConfirmationServiceImpl;

@Configuration
@PropertySource("classpath:mail.properties")
public class MailConfig {

    @Value("${spring.mail.address}")
    private String systemEmailAddress;

    @Value("${mailedconfirmations.url}")
    private String absoluteUrl;

    @Value("${mailedconfirmations.time}")
    private Duration confirmAliveTime;

    @Value("${mailedconfirmations.spam}")
    private Duration confirmSpamTime;

    @Bean
    public MailService mailService() {
        return new MailServiceImpl(systemEmailAddress);
    }

    @Bean
    public MailedConfirmationService mailedConfirmationService() {
        return new MailedConfirmationServiceImpl(absoluteUrl, confirmAliveTime.toMillis(),
                confirmSpamTime.toMillis());
    }

}
