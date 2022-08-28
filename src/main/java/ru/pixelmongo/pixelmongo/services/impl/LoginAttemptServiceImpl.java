package ru.pixelmongo.pixelmongo.services.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import ru.pixelmongo.pixelmongo.services.LoginAttemptService;

public class LoginAttemptServiceImpl implements LoginAttemptService{

    private final int maxAttempts;
    private final long protectTime;

    private Map<String, LoginAttemptCache> cache = new ConcurrentHashMap<>();
    private ScheduledExecutorService cleanTimer;

    public LoginAttemptServiceImpl(int maxAttempts, long protectTime) {
        this.maxAttempts = maxAttempts;
        this.protectTime = protectTime;
    }

    @PostConstruct
    public void init() {
        cleanTimer = Executors.newScheduledThreadPool(1, new CustomizableThreadFactory("login-attempt-cache-cleaner-"));
        cleanTimer.scheduleAtFixedRate(()->cache.values()
                .removeIf(c->c.lastUpdate<System.currentTimeMillis()-protectTime),
                30, 30, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void stop() {
        cleanTimer.shutdownNow();
        cache.clear();
    }

    @Override
    public boolean isBlocked(HttpServletRequest request) {
        if(protectTime == 0) return false;
        LoginAttemptCache cache = this.cache.get(request.getRemoteAddr());
        return cache != null && cache.isBlocked();
    }

    @Override
    public void cleanAttempts(HttpServletRequest request) {
        if(protectTime == 0) return;
        this.cache.remove(request.getRemoteAddr());
    }

    @Override
    public void onLoginFail(HttpServletRequest request) {
        if(protectTime == 0) return;
        this.cache.computeIfAbsent(request.getRemoteAddr(), s->new LoginAttemptCache()).addFail();
    }

    private class LoginAttemptCache{

        long lastUpdate;
        int count;

        void addFail() {
            long now = System.currentTimeMillis();
            if(lastUpdate + protectTime < now) count = 0;
            lastUpdate = now;
            ++count;
        }

        boolean isBlocked() {
            return count >= maxAttempts && lastUpdate + protectTime >= System.currentTimeMillis();
        }



    }

}
