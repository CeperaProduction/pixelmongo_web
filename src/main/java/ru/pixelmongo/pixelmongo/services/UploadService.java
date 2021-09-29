package ru.pixelmongo.pixelmongo.services;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    public static final Logger LOGGER = LogManager.getLogger(UploadService.class);

    public String getUploadURL();

    public String getUploadDir();

    public Path upload(MultipartFile mfile, String... path);

    public Path upload(String newFileName, MultipartFile mfile, String... path);

    public Path getUploadPath(String fileName, String... path);

    public String getUploadPathURL(String fileName, String... path);

    public String getUploadPathURLIfExists(String defautlUrl, String fileName, String... path);

    public boolean deleteUploaded(String fileName, String... path);

    /**
     * Delete following directory inside current upload directory.
     * Recursively deletes all nested content.
     * @param dirName
     * @param path
     * @return
     */
    public boolean deleteUploadedDirectory(String dirName, String... path);

    public Path uploadImageResized(MultipartFile mfile, int width, int height, boolean saveAsPng,
            String newFileName, String... path);

}
