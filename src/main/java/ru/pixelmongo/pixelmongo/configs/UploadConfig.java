package ru.pixelmongo.pixelmongo.configs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ru.pixelmongo.pixelmongo.services.UploadService;
import ru.pixelmongo.pixelmongo.services.impl.UploadServiceImpl;

@Configuration
@PropertySource("classpath:upload.properties")
public class UploadConfig {

    @Autowired
    private ApplicationContext ctx;

    @Value("${upload.handler.enabled}")
    private boolean handlerEnabled;

    @Value("${upload.handler.url}")
    private String uploadUrl;

    @Value("${upload.location.path}")
    private String path;

    private String uploadDir;

    @PostConstruct
    public void load() throws URISyntaxException, IOException {
        /*
        if(uploadUrl.endsWith("/"))
            uploadUrl = uploadUrl.substring(0, uploadUrl.length()-1);
        if(!path.endsWith("/")) path += "/";
        switch(protocol) {
        case classpath:
            URL r = this.getClass().getResource(path);
            uploadPath = Paths.get(r.toURI()).toAbsolutePath().toString();
            uploadPathFull = "classpath:"+path;
            break;
        case file:
            Path p = Paths.get(path).toAbsolutePath();
            uploadPath = p.toString();
            uploadPathFull = p.toUri().toString();
            break;
        }*/
        if(uploadUrl.endsWith("/"))
            uploadUrl = uploadUrl.substring(0, uploadUrl.length()-1);
        uploadDir = Paths.get(ctx.getResource(path).getURI()).toAbsolutePath().toString();
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public String getUploadHandlerResourcePath() {
        return path;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public boolean isHandlerEnabled() {
        return handlerEnabled;
    }

    @Bean
    public UploadService uploadService() {
        return new UploadServiceImpl(uploadUrl, uploadDir);
    }

}
