package ru.pixelmongo.pixelmongo.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

public class FileSizeValidator implements ConstraintValidator<FileSize, MultipartFile> {

    private long maxSize;

    @Override
    public void initialize(FileSize annotation) {
        this.maxSize = annotation.value();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return file == null || file.isEmpty() || file.getSize() <= maxSize;
    }

}
