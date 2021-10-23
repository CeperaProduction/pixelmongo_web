package ru.pixelmongo.pixelmongo.services.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;

@Service("popupMessages")
public class PopupMessageServiceImpl implements PopupMessageService{

    public static final String COOKIE_KEY = "message";
    public static final String REQUEST_ATTR_KEY = "sent_popup_messages";

    @Value("${server.servlet.context-path}")
    private String baseUrl;

    @Override
    public void sendUsingCookies(PopupMessage message,
            HttpServletRequest request,
            HttpServletResponse response) {

        List<PopupMessage> messages = getMessages(request);

        messages.add(message);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String msgCookie = mapper.writeValueAsString(messages);
            Cookie cookie = new Cookie(COOKIE_KEY, URLEncoder.encode(msgCookie, "UTF-8"));
            cookie.setPath(baseUrl);
            response.addCookie(cookie);
            request.setAttribute(REQUEST_ATTR_KEY, messages);
        }catch(Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    @SuppressWarnings("unchecked")
    private List<PopupMessage> getRequestMessages(HttpServletRequest request) {
        Object sentObj = request.getAttribute(REQUEST_ATTR_KEY);
        if(sentObj instanceof List) {
            return (List<PopupMessage>) sentObj;
        }
        return new ArrayList<PopupMessage>();
    }

    @Override
    public List<PopupMessage> getMessages(HttpServletRequest request){
        List<PopupMessage> messages = getMessagesFromCookies(request);
        messages.addAll(getRequestMessages(request));
        return messages;
    }

    @Override
    public List<PopupMessage> getMessagesFromCookies(HttpServletRequest request){

        ObjectMapper mapper = new ObjectMapper();

        String msgCookie = "[]";
        if(request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                if(cookie.getName().equals(COOKIE_KEY)) {
                    msgCookie = cookie.getValue();
                }
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
