package ru.pixelmongo.pixelmongo.repositories.primary.donate;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateQuery;

public interface DonateQueryRepository extends CrudRepository<DonateQuery, Integer>{

    public List<DonateQuery> findAllByServer(String serverConfigName);

    public List<DonateQuery> findAllByServerAndDoneFalse(String serverConfigName);

    public List<DonateQuery> findAllByServer(String serverConfigName, Pageable page);

}
