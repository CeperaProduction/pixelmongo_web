package ru.pixelmongo.pixelmongo.repositories.primary;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.IngameNewsChannel;

public interface IngameNewsChannelsRepository extends CrudRepository<IngameNewsChannel, Integer>{

    public Optional<IngameNewsChannel> findByName(String channelName);

    public List<IngameNewsChannel> findByNameIn(List<String> channelNames);

}
