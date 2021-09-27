package ru.pixelmongo.pixelmongo.model.dto.forms;

import org.springframework.web.multipart.MultipartFile;

import ru.pixelmongo.pixelmongo.validation.FileSize;
import ru.pixelmongo.pixelmongo.validation.FileType;

public class SkinUploadForm {

    @FileType({"image/png"})
    @FileSize(value = 512*1024)
    private MultipartFile skin;

    @FileType({"image/png"})
    @FileSize(value = 512*1024)
    private MultipartFile cape;

    public MultipartFile getSkin() {
        return skin;
    }

    public void setSkin(MultipartFile skin) {
        this.skin = skin;
    }

    public MultipartFile getCape() {
        return cape;
    }

    public void setCape(MultipartFile cape) {
        this.cape = cape;
    }



}
