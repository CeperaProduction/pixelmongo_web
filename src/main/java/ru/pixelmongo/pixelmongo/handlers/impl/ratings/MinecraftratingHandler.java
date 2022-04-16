package ru.pixelmongo.pixelmongo.handlers.impl.ratings;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.RatingHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.utils.EncodeUtils;

@Component("ratingMinecraftrating")
public class MinecraftratingHandler implements RatingHandler{

    @Value("${ratings.top.minecraftrating.enabled}")
    private boolean enabled;

    @Value("${ratings.top.minecraftrating.secret_key}")
    private String key;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "minecraftrating";
    }

    @Override
    public boolean isValidRequest(Map<String, String> params, Locale loc, HttpServletRequest request) {
        String checkLine = params.get("username")+params.get("timestamp")+key;
        String sign = params.get("signature");
        String validSign = EncodeUtils.sha1(checkLine);
        //RatingService.LOGGER.info("Check line: "+checkLine+" sign: "+sign+" valid_sign: "+validSign);
        return validSign.equals(sign);
    }

    @Override
    public String getUserName(Map<String, String> params, Locale loc, HttpServletRequest request) {
        return params.get("username");
    }

    @Override
    public String getSuccessMessage(User user, Map<String, String> params, Locale loc, HttpServletRequest request,
            HttpServletResponse response) {
        return "ok";
    }


}
