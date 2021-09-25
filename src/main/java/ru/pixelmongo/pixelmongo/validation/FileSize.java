package ru.pixelmongo.pixelmongo.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * File size validation. Confirms empty files. For empty check use {@link FileNotEmpty}
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {FileSizeValidator.class})
public @interface FileSize {

    public String message() default "Invalid file size";

    /**
     * Max size in bytes.
     * @return
     */
    public long value();

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

}
