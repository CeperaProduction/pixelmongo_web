package ru.pixelmongo.pixelmongo.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ConfirmedPasswordValidator.class})
public @interface CheckConfirmedPassword {

    public String message() default "{auth.password.not_same}";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

}
