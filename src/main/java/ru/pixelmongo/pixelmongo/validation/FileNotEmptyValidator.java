package ru.pixelmongo.pixelmongo.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

public class FileNotEmptyValidator implements ConstraintValidator<FileNotEmpty, MultipartFile> {

    @Override
    public void initialize(FileNotEmpty annotation) {

    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return !file.isEmpty();
    }

}
