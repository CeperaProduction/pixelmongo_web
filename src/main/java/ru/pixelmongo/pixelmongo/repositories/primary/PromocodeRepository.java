package ru.pixelmongo.pixelmongo.repositories.primary;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.Promocode;

public interface PromocodeRepository extends CrudRepository<Promocode, Integer>{

    public Optional<Promocode> findByCodeIgnoreCase(String code);

    public Page<Promocode> findAllByOrderByIdDesc(Pageable page);

}
