package ru.pixelmongo.pixelmongo.configs;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import ru.pixelmongo.pixelmongo.services.UploadService;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${spring.web.resources.add-mappings}")
    private boolean staticEnabled;

    @Autowired
    private UploadConfig uploadCfg;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if(staticEnabled) {
            registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/favicon.ico");
            registry.addResourceHandler("/robots.txt").addResourceLocations("classpath:/static/robots.txt");
            registry.addResourceHandler("/sitemap.xml").addResourceLocations("classpath:/static/sitemap.xml");
        }
        if(uploadCfg.isHandlerEnabled()) {
            String url = uploadCfg.getUploadUrl()+"/**";
            String path = uploadCfg.getUploadHandlerResourcePath();
            registry.addResourceHandler(url)
                .setCachePeriod(0)
                .addResourceLocations(path);
            UploadService.LOGGER.info("Uploaded resource handler registered with url pattern '"+url+"'");
        }
    }

    public boolean isStaticEnabled() {
        return staticEnabled;
    }

    @Bean
    public LayoutDialect layoutDialect() {
      return new LayoutDialect();
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

    @Bean
    public Locale defaultLocale() {
        return Locale.forLanguageTag("ru");
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(defaultLocale());
        return slr;
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

}
