package ru.pixelmongo.pixelmongo.configs;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ru.pixelmongo.pixelmongo.services.UploadService;
import ru.pixelmongo.pixelmongo.services.impl.UploadServiceImpl;

@Configuration
@PropertySource("classpath:upload.properties")
public class UploadConfig {

    @Value("${upload.url}")
    private String uploadUrl;

    @Value("${upload.location.protocol}")
    private LocationProtocol protocol;

    @Value("${upload.location.path}")
    private String path;

    private String uploadPath;

    private String uploadPathFull;

    @PostConstruct
    public void load() throws URISyntaxException {
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
        }
    }

    public String getUploadDir() {
        return uploadPath;
    }

    public String getUploadPath() {
        return uploadPathFull;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    @Bean
    public UploadService uploadService() {
        return new UploadServiceImpl(uploadUrl, uploadPath);
    }

    public enum LocationProtocol{
        classpath, file;
    }

}
