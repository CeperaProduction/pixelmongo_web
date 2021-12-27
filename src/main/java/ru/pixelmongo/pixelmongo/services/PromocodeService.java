package ru.pixelmongo.pixelmongo.services;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.pixelmongo.pixelmongo.exceptions.EmailNotConfirmedException;
import ru.pixelmongo.pixelmongo.exceptions.PromocodeException;
import ru.pixelmongo.pixelmongo.model.dao.primary.Promocode;
import ru.pixelmongo.pixelmongo.model.dao.primary.PromocodeActivation;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;

public interface PromocodeService {

    public Promocode activate(User user, Promocode promocode) throws PromocodeException, EmailNotConfirmedException;

    public Promocode activate(User user, String code) throws PromocodeException, EmailNotConfirmedException;

    public boolean isActivated(User user, Promocode promocode);

    public Optional<Promocode> get(String code);

    public Optional<Promocode> get(int promoId);

    public Page<Promocode> get(Pageable page);

    public Promocode save(Promocode promocode);

    public void remove(Promocode promocode);

    public Page<PromocodeActivation> getActivations(Promocode promocode, Pageable page);

    public boolean isBlocked(User user, HttpServletRequest request);

    public void onPromocodeFail(User user, HttpServletRequest request);

}
