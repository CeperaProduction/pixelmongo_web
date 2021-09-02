package ru.pixelmongo.pixelmongo.configs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("ru.pixelmongo.pixelmongo")
@PropertySource("classpath:config.properties")
public class SpringConfig {

}
