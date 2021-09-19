package ru.pixelmongo.pixelmongo.repositories.primary.donate;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePage;

public interface DonatePageRepository extends CrudRepository<DonatePage, Integer>{

    public List<DonatePage> findAllByOrderByOrdinary();

}
