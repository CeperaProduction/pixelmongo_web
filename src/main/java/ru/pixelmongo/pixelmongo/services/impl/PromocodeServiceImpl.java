package ru.pixelmongo.pixelmongo.services.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import ru.pixelmongo.pixelmongo.exceptions.EmailNotConfirmedException;
import ru.pixelmongo.pixelmongo.exceptions.PromocodeAlreadyUsedException;
import ru.pixelmongo.pixelmongo.exceptions.PromocodeException;
import ru.pixelmongo.pixelmongo.exceptions.PromocodeExpiredException;
import ru.pixelmongo.pixelmongo.exceptions.PromocodeMaxUsesException;
import ru.pixelmongo.pixelmongo.exceptions.PromocodeNotFoundException;
import ru.pixelmongo.pixelmongo.model.dao.primary.Promocode;
import ru.pixelmongo.pixelmongo.model.dao.primary.PromocodeActivation;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.repositories.primary.PromocodeActivationRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.PromocodeRepository;
import ru.pixelmongo.pixelmongo.services.PromocodeService;

public class PromocodeServiceImpl implements PromocodeService{

    @Autowired
    private PromocodeRepository promocodes;

    @Autowired
    private PromocodeActivationRepository activations;

    private final boolean requireEmailConfirm;
    private final int maxAttempts;
    private final long protectTime;

    private Map<String, PromocodeAttemptCache> cache = new ConcurrentHashMap<>();
    private ScheduledExecutorService cleanTimer;

    public PromocodeServiceImpl(boolean requireEmailConfirm, int maxAttempts, long protectTime) {
        this.requireEmailConfirm = requireEmailConfirm;
        this.maxAttempts = maxAttempts;
        this.protectTime = protectTime;
    }

    @PostConstruct
    public void init() {
        cleanTimer = Executors.newScheduledThreadPool(1, new CustomizableThreadFactory("promocode-attempt-cache-cleaner-"));
        cleanTimer.scheduleAtFixedRate(()->cache.values()
                .removeIf(c->c.lastUpdate<System.currentTimeMillis()-protectTime),
                30, 30, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void stop() {
        cleanTimer.shutdownNow();
        cache.clear();
        cleanTimer = null;
    }

    @Override
    public boolean isConfirmedEmailOnly() {
        return requireEmailConfirm;
    }

    @Override
    public boolean isBlocked(User user, HttpServletRequest request) {
        if(protectTime == 0) return false;
        PromocodeAttemptCache cache1 = this.cache.get("user:"+user.getId());
        if(cache1 != null && cache1.isBlocked()) return true;
        PromocodeAttemptCache cache2 = this.cache.get(request.getRemoteAddr());
        return cache2 != null && cache2.isBlocked();
    }

    @Override
    public void onPromocodeFail(User user, HttpServletRequest request) {
        if(protectTime == 0) return;
        this.cache.computeIfAbsent("user:"+user.getId(), s->new PromocodeAttemptCache()).addFail();
        this.cache.computeIfAbsent(request.getRemoteAddr(), s->new PromocodeAttemptCache()).addFail();
    }

    @Override
    public Promocode activate(User user, Promocode promocode) throws PromocodeException, EmailNotConfirmedException {
        if(requireEmailConfirm && !user.isEmailConfirmed())
            throw new EmailNotConfirmedException(user.getId(), user.getName(), user.getEmail());
        if(isActivated(user, promocode))
            throw new PromocodeAlreadyUsedException(promocode.getId(), promocode.getCode());
        if(promocode.getTimesUsed() >= promocode.getMaxUses())
            throw new PromocodeMaxUsesException(promocode.getId(), promocode.getCode());
        if(promocode.getEndDate() != null && promocode.getEndDate().getTime() < System.currentTimeMillis())
            throw new PromocodeExpiredException(promocode.getId(), promocode.getTitle());
        doActivation(user, promocode);
        return promocode;
    }

    @Transactional
    private void doActivation(User user, Promocode promocode) {
        PromocodeActivation activation = new PromocodeActivation(promocode, user);
        activation = activations.save(activation);
        promocode.setTimesUsed(promocode.getTimesUsed()+1);
        promocode.getActivations().add(activation);
        promocodes.save(promocode);
        user.setBonusBalance(user.getBonusBalance()+promocode.getValue());
    }

    @Override
    public Promocode activate(User user, String code) throws PromocodeException, EmailNotConfirmedException {
        Promocode promocode = get(code).orElseThrow(()->new PromocodeNotFoundException(code));
        return activate(user, promocode);
    }

    @Override
    public boolean isActivated(User user, Promocode promocode) {
        return activations.existsByPromocodeAndUser(promocode, user);
    }

    @Override
    public Optional<Promocode> get(String code) {
        return promocodes.findByCodeIgnoreCase(code);
    }

    @Override
    public Optional<Promocode> get(int promoId) {
        return promocodes.findById(promoId);
    }

    @Override
    public Page<Promocode> get(Pageable page) {
        return promocodes.findAllByOrderByIdDesc(page);
    }

    @Override
    public Promocode save(Promocode promocode) {
        return promocodes.save(promocode);
    }

    @Override
    public void remove(Promocode promocode) {
        promocodes.delete(promocode);
    }

    @Override
    public Page<PromocodeActivation> getActivations(Promocode promocode, Pageable page) {
        return activations.findByPromocodeOrderByDate(promocode, page);
    }

    private class PromocodeAttemptCache{

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
