package ru.pixelmongo.pixelmongo.services.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;

@Service("popupMessages")
public class PopupMessageServiceImpl implements PopupMessageService{

    public static final String COOKIE_KEY = "message";

    @Override
    public void sendUsingCookies(PopupMessage message,
            HttpServletRequest request,
            HttpServletResponse response) {

        List<PopupMessage> messages = getMessagesFromCookies(request);
        messages.add(message);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String msgCookie = mapper.writeValueAsString(messages);
            Cookie cookie = new Cookie(COOKIE_KEY, URLEncoder.encode(msgCookie, "UTF-8"));
            cookie.setPath("/");
            response.addCookie(cookie);
        }catch(Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public List<PopupMessage> getMessagesFromCookies(HttpServletRequest request){

        ObjectMapper mapper = new ObjectMapper();

        String msgCookie = "[]";
        for(Cookie cookie : request.getCookies()) {
            if(cookie.getName().equals(COOKIE_KEY)) {
                msgCookie = cookie.getValue();
            }
        }

        List<PopupMessage> messages = new ArrayList<>();
        try {
            PopupMessage[] msgArr = mapper.readValue(msgCookie, PopupMessage[].class);
            messages.addAll(Arrays.asList(msgArr));
        }catch(JsonProcessingException ex) {}

        return messages;
    }

}
