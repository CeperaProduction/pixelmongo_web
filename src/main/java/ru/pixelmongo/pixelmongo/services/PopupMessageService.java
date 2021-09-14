package ru.pixelmongo.pixelmongo.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;

public interface PopupMessageService {

    /**
     * Takes {@link HttpServletRequest} and {@link HttpServletResponse}
     * from {@link RequestContextHolder#getRequestAttributes()}
     * @param message
     */
    public default void sendUsingCookies(PopupMessage message) {
        ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        sendUsingCookies(message, attr.getRequest(), attr.getResponse());
    }

    public void sendUsingCookies(PopupMessage message,
            HttpServletRequest request,
            HttpServletResponse response);

    public List<PopupMessage> getMessagesFromCookies(HttpServletRequest request);

}
