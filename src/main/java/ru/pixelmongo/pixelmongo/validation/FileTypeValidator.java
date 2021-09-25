package ru.pixelmongo.pixelmongo.validation;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

public class FileTypeValidator implements ConstraintValidator<FileType, MultipartFile> {

    private Set<String> validTypes;

    @Override
    public void initialize(FileType annotation) {
        validTypes = new HashSet<String>();
        for(String type : annotation.value())
            validTypes.add(type);
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return file.isEmpty() || validTypes.contains(file.getContentType());
    }

}
