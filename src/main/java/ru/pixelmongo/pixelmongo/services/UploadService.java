package ru.pixelmongo.pixelmongo.services;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    public static final Logger LOGGER = LogManager.getLogger(UploadService.class);

    public String getUploadUrl();

    public String getUploadDir();

    public Path upload(MultipartFile mfile, String... path);

    public Path upload(MultipartFile mfile, String newFileName, String... path);

    public boolean deleteUploaded(String fileName, String... path);

    public Path uploadImageResized(MultipartFile mfile, int width, int height, boolean saveAsPng,
            String newFileName, String... path);

}
