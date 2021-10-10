package ru.pixelmongo.pixelmongo.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {FileNotEmptyValidator.class})
public @interface FileNotEmpty {

    public String message() default "{upload.file.required}";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

}
