package ru.pixelmongo.pixelmongo.services;

import ru.pixelmongo.pixelmongo.exceptions.InvalidCaptchaException;

public interface CaptchaService {

    public String getPublicKey();

    public void processResponse(String response, String clientIp) throws InvalidCaptchaException;

}
