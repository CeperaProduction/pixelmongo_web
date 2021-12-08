package ru.pixelmongo.pixelmongo.services.impl;

import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.util.UriComponentsBuilder;

import ru.pixelmongo.pixelmongo.exceptions.MailedConfirmationExpiredException;
import ru.pixelmongo.pixelmongo.exceptions.MailedConfirmationHandlerNotFoundException;
import ru.pixelmongo.pixelmongo.exceptions.MailedConfirmationNotFoundException;
import ru.pixelmongo.pixelmongo.handlers.MailedConfirmationHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmation;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmationType;
import ru.pixelmongo.pixelmongo.repositories.primary.MailedConfirmationRepository;
import ru.pixelmongo.pixelmongo.services.MailService;
import ru.pixelmongo.pixelmongo.services.MailedConfirmationService;

public class MailedConfirmationServiceImpl implements MailedConfirmationService {

    @Autowired
    private MailService mail;

    @Autowired
    private MailedConfirmationRepository confirms;

    private Map<MailedConfirmationType, MailedConfirmationHandler<?>> handlers = new HashMap<>();

    @Autowired
    public void setupHandlers(List<MailedConfirmationHandler<?>> handlers) {
        handlers.forEach(h->this.handlers.put(h.getType(), h));
    }

    private final String ABSOLUTE_URL_BASE;
    private final long CONFIRMATION_ALIVE_TIME;
    private final long CONFIRMATION_SPAM_TIME;

    private static final String SESSION_LAST_CONFIRM_KEY = "mconfirm_last";

    public MailedConfirmationServiceImpl(String absoluteUrlBase, long confirmationAliveTime, long confirmationSpamTime) {
        if(absoluteUrlBase.endsWith("/"))
            absoluteUrlBase = absoluteUrlBase.substring(0, absoluteUrlBase.length()-1);
        this.ABSOLUTE_URL_BASE = absoluteUrlBase;
        this.CONFIRMATION_ALIVE_TIME = confirmationAliveTime;
        this.CONFIRMATION_SPAM_TIME = confirmationSpamTime;
    }

    @PostConstruct
    public void init() {
        confirms.deleteByCreateDateLessThan(new Date(System.currentTimeMillis()-CONFIRMATION_ALIVE_TIME));
        LOGGER.info("All expired mailed confirmations removed");
    }

    @SuppressWarnings("unchecked")
    public <T extends MailedConfirmation> Optional<MailedConfirmationHandler<T>> getConfirmationHandler(MailedConfirmationType type){
        MailedConfirmationHandler<T> handler = (MailedConfirmationHandler<T>) handlers.get(type);
        return Optional.ofNullable(handler);
    }

    @Override
    public <T extends MailedConfirmation> T sendConfirmation(MailedConfirmationType type,
            User user, Locale loc, HttpServletRequest request, Object... args) {
        MailedConfirmationHandler<T> handler = this.<T>getConfirmationHandler(type)
                .orElseThrow(()->new MailedConfirmationHandlerNotFoundException(type.name()));
        String key = generateKey();
        T confirm = handler.makeConfirmation(key, user, args);
        confirm = confirms.save(confirm);

        String title = handler.getMailTitle(loc);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(ABSOLUTE_URL_BASE+"/mconfirm");
        uriBuilder.queryParam("key", key);
        String link = uriBuilder.toUriString();

        String template = "mail/"+handler.getMailTemplate();
        Map<String, Object> context = new HashMap<>();
        context.put("user", user);
        context.put("confirm", confirm);
        context.put("title", title);
        context.put("baseUrl", ABSOLUTE_URL_BASE);
        context.put("link", link);

        try {
            mail.sendTemplatedMail(user.getEmail(), title, template, context, (helper)->{
                Resource resource = new ClassPathResource("/static/img/mail/logo.png");
                String contentType = helper.getFileTypeMap().getContentType(resource.getFilename());
                contentType = contentType.replace("x-png", "png");
                helper.addInline("pgo-logo", resource, contentType);
            });
        } catch (Exception ex) {
            MailService.LOGGER.catching(ex);
        }

        if(request != null && CONFIRMATION_SPAM_TIME != 0) {
            HttpSession session = request.getSession();
            Object dataObj = session.getAttribute(SESSION_LAST_CONFIRM_KEY);
            LastConfirmSessionData data;
            if(dataObj instanceof LastConfirmSessionData)
                data = (LastConfirmSessionData) dataObj;
            else {
                data = new LastConfirmSessionData();
                session.setAttribute(SESSION_LAST_CONFIRM_KEY, data);
            }
            data.put(type, confirm.getCreateDate().getTime());
        }

        return confirm;
    }

    private String generateKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String processConfirmation(String key, Locale locale, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        MailedConfirmation confirm = confirms.findByKey(key)
                    .orElseThrow(()->new MailedConfirmationNotFoundException());
        if(!checkAliveTime(confirm)) {
            confirms.delete(confirm);
            throw new MailedConfirmationExpiredException();
        }
        MailedConfirmationHandler handler = getConfirmationHandler(confirm.getType())
                .orElseThrow(()->new MailedConfirmationHandlerNotFoundException(confirm.getType().name()));

        String redirect;
        try {
            redirect = handler.processConfirmation(confirm, locale, request, response);
            switch(handler.consumeAfterProcess()) {
            case CURRENT:
                confirms.delete(confirm);
                break;
            case ALL:
                confirms.deleteByUserIdAndType(confirm.getUserId(), confirm.getType());
                break;
            default:
            }
        }catch(Exception ex) {
            switch(handler.consumeOnException()) {
            case CURRENT:
                confirms.delete(confirm);
                break;
            case ALL:
                confirms.deleteByUserIdAndType(confirm.getUserId(), confirm.getType());
                break;
            default:
            }
            throw ex;
        }
        return redirect;
    }

    private boolean checkAliveTime(MailedConfirmation confirmation) {
        return confirmation.getCreateDate().getTime()+CONFIRMATION_ALIVE_TIME >= System.currentTimeMillis();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends MailedConfirmation> Optional<T> getActiveConfirmation(String key, MailedConfirmationType type){
        MailedConfirmation confirm = confirms.findByKey(key).orElse(null);
        if(confirm != null && confirm.getType() == type && checkAliveTime(confirm))
            return Optional.of((T)confirm);
        return Optional.empty();
    }

    @Override
    public void consumeConfirmation(MailedConfirmation confirmation) {
        confirms.delete(confirmation);
    }

    @Override
    public void consumeConfirmations(int userId, MailedConfirmationType type) {
        confirms.deleteByUserIdAndType(userId, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends MailedConfirmation> Optional<T> getLastConfirmation(int userId, MailedConfirmationType type) {
        MailedConfirmation confirm = confirms.findTopByUserIdAndTypeOrderByCreateDateDesc(userId, type).orElse(null);
        if(confirm != null && checkAliveTime(confirm))
            return Optional.of((T)confirm);
        return Optional.empty();
    }

    @Override
    public long checkForSpam(int userId, MailedConfirmationType type, HttpServletRequest request) {
        if(userId == 0 && request == null)
            throw new IllegalArgumentException("User id or request object must be providen");
        if(CONFIRMATION_SPAM_TIME == 0)
            return 0;
        long now = System.currentTimeMillis();
        long time1 = 0, time2 = 0;
        if(request != null) {
            HttpSession session = request.getSession(false);
            if(session != null) {
                Object dataObj = session.getAttribute(SESSION_LAST_CONFIRM_KEY);
                if(dataObj instanceof LastConfirmSessionData) {
                    LastConfirmSessionData data = (LastConfirmSessionData) dataObj;
                    time1 = data.get(type)+CONFIRMATION_SPAM_TIME-now;
                }
            }
        }
        if(userId != 0) {
            long last = getLastConfirmation(userId, type)
                    .map(MailedConfirmation::getCreateDate)
                    .map(Date::getTime).orElse(0L);
            time2 = last+CONFIRMATION_SPAM_TIME-now;

        }

        return Math.max(Math.max(time1, time2), 0);
    }

    @Override
    public String printAwaitTime(long awaitTimeMs) {
        int t = (int)(awaitTimeMs/1000);
        if(t < 0) return "00:00";
        int h = t/3600;
        int m = (t / 60) % 60;
        int s = t % 60;
        String hs = h > 0 ? h+":" : "";
        return hs + String.format("%02d:%02d", m, s);
    }

    private class LastConfirmSessionData{

        Map<MailedConfirmationType, Long> times = new EnumMap<>(MailedConfirmationType.class);

        long get(MailedConfirmationType type) {
            return times.getOrDefault(type, 0L);
        }

        void put(MailedConfirmationType type, long time) {
            times.put(type, time);
        }

    }


}
