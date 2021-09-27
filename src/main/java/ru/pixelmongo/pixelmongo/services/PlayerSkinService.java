package ru.pixelmongo.pixelmongo.services;

import org.springframework.web.multipart.MultipartFile;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;

public interface PlayerSkinService {

    public void uploadSkin(MultipartFile mfile, User user, boolean allowHD);

    public void uploadCape(MultipartFile mfile, User user, boolean allowHD);

    public boolean deleteSkin(User user);

    public boolean deleteCape(User user);

    public boolean isValidSkinSize(int width, int height, boolean allowHD);

    public boolean isValidCapeSize(int width, int height, boolean allowHD);

    public String getSkinUrlPath(User user);

    public String getCapeUrlPath(User user);

    public String getSkinFaceUrlPath(User user);

    public String getSkinBodyUrlPath(User user);
}
