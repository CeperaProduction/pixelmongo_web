package ru.pixelmongo.pixelmongo.repositories.primary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.Promocode;
import ru.pixelmongo.pixelmongo.model.dao.primary.PromocodeActivation;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;

public interface PromocodeActivationRepository extends CrudRepository<PromocodeActivation, Integer>{

    public Page<PromocodeActivation> findByPromocodeOrderByDate(Promocode promocode, Pageable page);

    public boolean existsByPromocodeAndUser(Promocode promocode, User user);

}
