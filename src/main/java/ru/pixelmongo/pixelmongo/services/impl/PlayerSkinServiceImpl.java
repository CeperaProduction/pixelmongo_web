package ru.pixelmongo.pixelmongo.services.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.pixelmongo.pixelmongo.exceptions.WrongFileTypeException;
import ru.pixelmongo.pixelmongo.exceptions.WrongImageSizeException;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.services.PlayerSkinService;
import ru.pixelmongo.pixelmongo.services.UploadService;

@Service("skinService")
public class PlayerSkinServiceImpl implements PlayerSkinService{

    @Autowired
    private UploadService upload;

    @Override
    public void uploadSkin(MultipartFile mfile, User user, boolean allowHD) {
        if(!"image/png".equals(mfile.getContentType()))
            throw new WrongFileTypeException(mfile, "image/png");

        try {

            ByteArrayInputStream bin = new ByteArrayInputStream(mfile.getBytes());
            BufferedImage inImage = ImageIO.read(bin);

            if(!isValidSkinSize(inImage.getWidth(), inImage.getHeight(), allowHD))
                throw new WrongImageSizeException(mfile);

            //Save skin
            upload.upload(user.getName()+".png", mfile, "skins", "skins");

            int scale = inImage.getWidth() / 64;

            //Save face
            BufferedImage face = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gFace = face.createGraphics();
            copy(gFace, inImage, 0, 0, 256, 256, 8, 8, 8, 8, scale);
            copy(gFace, inImage, 0, 0, 256, 256, 40, 8, 8, 8, scale);
            gFace.dispose();
            save(face, user.getName()+".png", "skins", "faces");

            //Save body
            BufferedImage body = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gBody = body.createGraphics();
            copy(gBody, inImage, 32, 0, 64, 64, 8, 8, 8, 8, scale);
            copy(gBody, inImage, 32, 0, 64, 64, 40, 8, 8, 8, scale);
            copy(gBody, inImage, 32, 64, 64, 96, 20, 20, 8, 12, scale);
            copy(gBody, inImage, 64, 160, 32, 96, 4, 20, 4, 12, scale);
            copy(gBody, inImage, 32, 160, 32, 96, 4, 20, 4, 12, scale);
            copy(gBody, inImage, 96, 64, 32, 96, 44, 20, 4, 12, scale);
            copy(gBody, inImage, 0, 64, 32, 96, 44, 20, 4, 12, scale);

            copy(gBody, inImage, 160, 0, 64, 64, 24, 8, 8, 8, scale);
            copy(gBody, inImage, 160, 0, 64, 64, 56, 8, 8, 8, scale);
            copy(gBody, inImage, 160, 64, 64, 96, 32, 20, 8, 12, scale);
            copy(gBody, inImage, 192, 160, 32, 96, 12, 20, 4, 12, scale);
            copy(gBody, inImage, 160, 160, 32, 96, 12, 20, 4, 12, scale);
            copy(gBody, inImage, 224, 64, 32, 96, 52, 20, 4, 12, scale);
            copy(gBody, inImage, 128, 64, 32, 96, 52, 20, 4, 12, scale);
            gBody.dispose();
            save(body, user.getName()+".png", "skins", "bodies");

        }catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void copy(Graphics2D gTarget, BufferedImage source, int dx, int dy, int dw, int dh, int sx, int sy, int sw, int sh, int scale) {
        gTarget.drawImage(source, dx, dy, dx+dw, dy+dh, sx*scale, sy*scale, (sx+sw)*scale, (sy+sh)*scale, null);
    }

    private void save(BufferedImage image, String name, String... path) throws IOException{
        Path outPath = upload.getUploadPath(name, path);
        File outFile = outPath.toFile();
        outFile.getParentFile().mkdirs();
        outFile.createNewFile();
        ImageIO.write(image, "png", outFile);
    }

    @Override
    public boolean isValidSkinSize(int width, int height, boolean allowHD) {
        if(width != height && width != height * 2)
            return false;
        if(width == 64) return true;
        if(allowHD) {
            switch(width) {
            case 128: case 256: case 512: case 1024: return true;
            }
        }
        return false;
    }

    @Override
    public void uploadCape(MultipartFile mfile, User user, boolean allowHD) {
        if(!"image/png".equals(mfile.getContentType()))
            throw new WrongFileTypeException(mfile, "image/png");

        try {

            ByteArrayInputStream bin = new ByteArrayInputStream(mfile.getBytes());
            BufferedImage inImage = ImageIO.read(bin);

            if(!isValidCapeSize(inImage.getWidth(), inImage.getHeight(), allowHD))
                throw new WrongImageSizeException(mfile);

            //Save cape
            upload.upload(user.getName()+".png", mfile, "skins", "capes");

        }catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isValidCapeSize(int width, int height, boolean allowHD) {
        if(width == 22 && height == 17) return true;
        if(width != height * 2)
            return false;
        if(width == 64) return true;
        if(allowHD) {
            switch(width) {
            case 128: case 256: case 512: case 1024: return true;
            }
        }
        return false;
    }

    @Override
    public String getSkinUrlPath(User user) {
        return upload.getUploadPathURLIfExists("/img/skins/default_skin.png",
                user.getName()+".png", "skins", "skins");
    }

    @Override
    public String getCapeUrlPath(User user) {
        return upload.getUploadPathURLIfExists("/img/skins/default_cape.png",
                user.getName()+".png", "skins", "capes");
    }

    @Override
    public String getSkinFaceUrlPath(User user) {
        return upload.getUploadPathURLIfExists("/img/skins/default_face.png",
                user.getName()+".png", "skins", "faces");
    }

    @Override
    public String getSkinBodyUrlPath(User user) {
        return upload.getUploadPathURLIfExists("/img/skins/default_body.png",
                user.getName()+".png", "skins", "bodies");
    }

    @Override
    public boolean deleteSkin(User user) {
        try {
            boolean b1 = Files.deleteIfExists(upload.getUploadPath(user.getName()+".png", "skins", "skins"));
            boolean b2 = Files.deleteIfExists(upload.getUploadPath(user.getName()+".png", "skins", "faces"));
            boolean b3 = Files.deleteIfExists(upload.getUploadPath(user.getName()+".png", "skins", "bodies"));
            return b1 || b2 || b3;
        }catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean deleteCape(User user) {
        try {
            return Files.deleteIfExists(upload.getUploadPath(user.getName()+".png", "skins", "capes"));
        }catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
