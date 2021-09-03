package ru.pixelmongo.pixelmongo.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.pixelmongo.pixelmongo.model.MonitoringServer;

@Repository
public interface MonitoringServerRepository extends CrudRepository<MonitoringServer, Integer>{

    public Optional<MonitoringServer> findByTag(String serverTag);
    
}
