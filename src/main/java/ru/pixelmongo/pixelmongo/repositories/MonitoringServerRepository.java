package ru.pixelmongo.pixelmongo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.pixelmongo.pixelmongo.model.dao.MonitoringServer;

@Repository
public interface MonitoringServerRepository extends CrudRepository<MonitoringServer, Integer>{

    public Optional<MonitoringServer> findByTag(String serverTag);

    public Iterable<MonitoringServer> findAllByOrderByOrdinaryAsc();

    @Query("SELECT max(s.ordinary) FROM MonitoringServer s")
    public int getMaxServerOrdinary();

}
