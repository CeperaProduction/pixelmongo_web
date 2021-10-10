package ru.pixelmongo.pixelmongo.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
*
* File Type validation. Confirms empty files. For empty check use {@link FileNotEmpty}
*
*/
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {FileTypeValidator.class})
public @interface FileType {

    public String message() default "{upload.file.type.invalid}";

    /**
     * List of supported MIME types. Example: "image/png"
     * @return
     */
    public String[] value();

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

}
