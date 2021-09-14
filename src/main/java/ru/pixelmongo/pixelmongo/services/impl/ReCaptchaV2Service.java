package ru.pixelmongo.pixelmongo.services.impl;

import java.net.URI;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import ru.pixelmongo.pixelmongo.exceptions.InvalidCaptchaEcxeption;
import ru.pixelmongo.pixelmongo.model.dto.ReCaptchaResponse;
import ru.pixelmongo.pixelmongo.services.CaptchaService;

public class ReCaptchaV2Service implements CaptchaService{

    private final String publicKey;

    private final String secretKey;

    @Autowired
    private RestOperations rest;

    private static String GOOGLE_URL_PATTERN
            = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    private static final Logger LOGGER = LogManager.getLogger(ReCaptchaV2Service.class);

    public ReCaptchaV2Service(String publicKey, String secretKey) {
        this.publicKey = publicKey;
        this.secretKey = secretKey;
    }

    @Override
    public String getPublicKey() {
        return publicKey;
    }

    @Override
    public void processResponse(String response, String clientIp) throws InvalidCaptchaEcxeption {
        if(!checkResponsePattern(response)) {
            LOGGER.debug("Invalid response pattern: "+response);
            throw new InvalidCaptchaEcxeption("Response contains invalid characters");
        }

        URI verifyUri = URI.create(String.format(GOOGLE_URL_PATTERN, secretKey, response, clientIp));

        ReCaptchaResponse googleResponse = rest.getForObject(verifyUri, ReCaptchaResponse.class);

        LOGGER.debug("Google response: "+googleResponse.toString());

        if(!googleResponse.isSuccess()) {
            throw new InvalidCaptchaEcxeption("reCaptcha was not successfully validated");
        }
    }

    private boolean checkResponsePattern(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

}
