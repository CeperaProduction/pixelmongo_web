package ru.pixelmongo.pixelmongo.exceptions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class WrongFileTypeException extends IllegalArgumentException{

    private static final long serialVersionUID = -6154407435107810673L;

    private final MultipartFile file;
    private final List<String> validTypes;

    public WrongFileTypeException(MultipartFile file, String... validTypes) {
        super("Wrong file type '"+file.getContentType()+"'. Must be one of "+Arrays.toString(validTypes));
        this.file = file;
        this.validTypes = Collections.unmodifiableList(Arrays.asList(validTypes));
    }

    public MultipartFile getFile() {
        return file;
    }

    public List<String> getValidTypes() {
        return validTypes;
    }

}
