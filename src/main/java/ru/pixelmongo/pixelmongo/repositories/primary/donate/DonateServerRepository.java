package ru.pixelmongo.pixelmongo.repositories.primary.donate;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateServer;

public interface DonateServerRepository extends CrudRepository<DonateServer, Integer>{

    public Optional<DonateServer> findByConfigName(String configName);

}
