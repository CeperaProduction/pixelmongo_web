package ru.pixelmongo.pixelmongo.repositories.primary.donate;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePage;
import ru.pixelmongo.pixelmongo.repositories.OrderedDataRepository;

public interface DonatePageRepository extends CrudRepository<DonatePage, Integer>,
    OrderedDataRepository<DonatePage, Integer>{

    public Optional<DonatePage> findByTag(String tag);

}
