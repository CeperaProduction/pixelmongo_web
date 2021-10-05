package ru.pixelmongo.pixelmongo.model.dto;

import ru.pixelmongo.pixelmongo.validation.CheckConfirmedPassword;

@CheckConfirmedPassword
public interface ConfirmedPassword {

    public String getPassword();

    public String getPasswordRepeat();

}
