package ru.pixelmongo.pixelmongo.repositories.primary;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.MonitoringServer;
import ru.pixelmongo.pixelmongo.repositories.OrderedDataRepository;

public interface MonitoringServerRepository extends CrudRepository<MonitoringServer, Integer>,
    OrderedDataRepository<MonitoringServer, Integer>{

    public Optional<MonitoringServer> findByTag(String serverTag);

}
