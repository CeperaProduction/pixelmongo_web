package ru.pixelmongo.pixelmongo.handlers.impl.ratings;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.pixelmongo.pixelmongo.handlers.RatingHandler;
import ru.pixelmongo.pixelmongo.utils.EncodeUtils;

@Component("ratingMcrate")
public class McrateHandler implements RatingHandler{

    @Value("${ratings.top.mcrate.enabled}")
    private boolean enabled;

    @Value("${ratings.top.mcrate.secret_key}")
    private String key;

    @Override
    public boolean useJson() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "mcrate";
    }

    @Override
    public boolean isValidRequest(Map<String, String> params, Locale loc, HttpServletRequest request) {
        String checkLine = params.get("nick")+key+"mcrate";
        String sign = params.get("hash");
        return EncodeUtils.md5(checkLine, 2).equals(sign);
    }

    @Override
    public String getUserName(Map<String, String> params, Locale loc, HttpServletRequest request) {
        return params.get("nick");
    }


}
