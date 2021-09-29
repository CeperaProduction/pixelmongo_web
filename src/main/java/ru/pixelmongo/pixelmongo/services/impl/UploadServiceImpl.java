package ru.pixelmongo.pixelmongo.services.impl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.groovy.util.Arrays;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import ru.pixelmongo.pixelmongo.exceptions.WrongFileTypeException;
import ru.pixelmongo.pixelmongo.services.UploadService;

public class UploadServiceImpl implements UploadService{

    private final String uploadUrlPath;
    private final String uploadDirPath;

    public UploadServiceImpl(String uploadUrl, String uploadDir) {
        this.uploadUrlPath = uploadUrl;
        this.uploadDirPath = uploadDir;
        LOGGER.info("Upload root url: "+this.uploadUrlPath);
        LOGGER.info("Upload root dir: "+this.uploadDirPath);
    }

    @Override
    public String getUploadURL() {
        return uploadUrlPath;
    }

    @Override
    public String getUploadDir() {
        return uploadDirPath;
    }

    @Override
    public Path upload(MultipartFile mfile, String... path){
        return upload(mfile.getOriginalFilename(), mfile, path);
    }

    @Override
    public Path upload(String newFileName, MultipartFile mfile, String... path){
        try {
            Path p = getUploadPath(newFileName, path);
            if(!Files.exists(p)) {
                p.toFile().getParentFile().mkdirs();
                Files.createFile(p);
            }
            mfile.transferTo(p);
            return p;
        }catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path getUploadPath(String fileName, String... path) {
        return Paths.get(uploadDirPath, Arrays.concat(path, new String[] {fileName}));
    }

    @Override
    public String getUploadPathURL(String fileName, String... path) {
        StringBuilder sb = new StringBuilder(this.uploadUrlPath);
        for(String p : path) {
            sb.append('/').append(p);
        }
        sb.append('/').append(fileName);
        return sb.toString();
    }

    @Override
    public String getUploadPathURLIfExists(String defautlUrl, String fileName, String... path) {
        Path p = getUploadPath(fileName, path);
        if(Files.exists(p)) {
            return getUploadPathURL(fileName, path);
        }
        return defautlUrl;
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
    public boolean deleteUploadedDirectory(String dirName, String... path) {
        Path p = getUploadPath(dirName, path);
        try {
            return FileSystemUtils.deleteRecursively(p);
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
                throw new WrongFileTypeException(mfile, "image/png", "image/jpg", "image/jpeg");
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
