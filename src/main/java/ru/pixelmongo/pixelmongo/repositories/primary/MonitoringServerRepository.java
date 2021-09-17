package ru.pixelmongo.pixelmongo.repositories.primary;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.MonitoringServer;

public interface MonitoringServerRepository extends CrudRepository<MonitoringServer, Integer>{

    public Optional<MonitoringServer> findByTag(String serverTag);

    public Iterable<MonitoringServer> findAllByOrderByOrdinaryAsc();

    @Query("SELECT max(s.ordinary) FROM MonitoringServer s")
    public int getMaxServerOrdinary();

}
