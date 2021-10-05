package ru.pixelmongo.pixelmongo.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ru.pixelmongo.pixelmongo.model.dto.ConfirmedPassword;

public class ConfirmedPasswordValidator implements ConstraintValidator<CheckConfirmedPassword, ConfirmedPassword>{

    @Override
    public boolean isValid(ConfirmedPassword value, ConstraintValidatorContext context) {
        return value.getPassword() == null || value.getPassword().equals(value.getPasswordRepeat());
    }

}
