package ru.pixelmongo.pixelmongo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.pixelmongo.pixelmongo.data.MonitoringServer;

@Repository
public interface MonitoringServerRepository extends CrudRepository<MonitoringServer, Integer>{

	public MonitoringServer findByTag(String serverTag);
	
}
