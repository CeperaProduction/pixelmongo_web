package ru.pixelmongo.pixelmongo.services.impl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.groovy.util.Arrays;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.pixelmongo.pixelmongo.services.UploadService;

@Service("uploadService")
public class UploadServiceImpl implements UploadService{

    private String uploadUrlPath;
    private String uploadDirPath;

    @PostConstruct
    public void init() {
        this.uploadUrlPath = "/uploads";
        URL r = this.getClass().getResource(this.uploadUrlPath);
        try {
            this.uploadDirPath = Paths.get(r.toURI()).toAbsolutePath().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Upload root url: "+this.uploadUrlPath);
        LOGGER.info("Upload root dir: "+this.uploadDirPath);
    }

    @Override
    public String getUploadUrl() {
        return uploadUrlPath;
    }

    @Override
    public String getUploadDir() {
        return uploadDirPath;
    }

    @Override
    public Path upload(MultipartFile mfile, String... path){
        return upload(mfile, mfile.getOriginalFilename(), path);
    }

    @Override
    public Path upload(MultipartFile mfile, String newFileName, String... path){
        try {
            Path p = getUploadPath(newFileName, path);
            mfile.transferTo(p);
            return p;
        }catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getUploadPath(String fileName, String... path) {
        return Paths.get(uploadDirPath, Arrays.concat(path, new String[] {fileName}));
    }

    @Override
    public boolean deleteUploaded(String fileName, String... path) {
        Path p = getUploadPath(fileName, path);
        try {
            return Files.deleteIfExists(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path uploadImageResized(MultipartFile mfile, int width, int height, boolean saveAsPng,
            String newFileName, String... path) {
        try {

            boolean png = "image/png".equals(mfile.getContentType());
            if(!png
                    && !"image/jpeg".equals(mfile.getContentType())
                    && !"image/jpg".equals(mfile.getContentType())) {
                throw new IllegalArgumentException("File must be png or jpeg image");
            }

            ByteArrayInputStream bin = new ByteArrayInputStream(mfile.getBytes());
            BufferedImage inImage = ImageIO.read(bin);

            Image resizedImage = inImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            int outType = png && saveAsPng ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
            BufferedImage outImage = new BufferedImage(width, height, outType);

            Graphics2D g = outImage.createGraphics();
            g.drawImage(resizedImage, 0, 0, null);
            g.dispose();

            Path outPath = getUploadPath(newFileName, path);
            File outFile = outPath.toFile();
            outFile.getParentFile().mkdirs();
            outFile.createNewFile();
            ImageIO.write(outImage, saveAsPng ? "png" : "jpg", outFile);
            return outPath;

        }catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }


}
