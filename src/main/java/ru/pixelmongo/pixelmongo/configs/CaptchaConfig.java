package ru.pixelmongo.pixelmongo.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ru.pixelmongo.pixelmongo.services.CaptchaService;
import ru.pixelmongo.pixelmongo.services.impl.ReCaptchaV2Service;

@Configuration
@PropertySource("classpath:recaptcha.properties")
public class CaptchaConfig {

    @Value("${google.recaptcha.v2.key.public}")
    private String publicKey;

    @Value("${google.recaptcha.v2.key.secret}")
    private String secretKey;

    @Bean
    public CaptchaService captchaService() {
        return new ReCaptchaV2Service(publicKey, secretKey);
    }

}
