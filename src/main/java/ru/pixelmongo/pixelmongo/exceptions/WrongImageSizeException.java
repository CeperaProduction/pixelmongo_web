package ru.pixelmongo.pixelmongo.exceptions;

import org.springframework.web.multipart.MultipartFile;

public class WrongImageSizeException extends IllegalArgumentException{

    private static final long serialVersionUID = -3960700731398587638L;

    private final MultipartFile file;

    public WrongImageSizeException(MultipartFile file) {
        super("Wrong image size");
        this.file = file;
    }

    public MultipartFile getFile() {
        return file;
    }

}
