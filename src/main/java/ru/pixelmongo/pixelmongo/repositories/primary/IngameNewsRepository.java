package ru.pixelmongo.pixelmongo.repositories.primary;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.pixelmongo.pixelmongo.model.dao.primary.IngameNews;
import ru.pixelmongo.pixelmongo.model.dao.primary.IngameNewsChannel;

public interface IngameNewsRepository extends CrudRepository<IngameNews, Integer>{

    public List<IngameNews> findAllByOrderByCreateDateDesc();

    public List<IngameNews> findByChannelOrderByCreateDateDesc(IngameNewsChannel channel);

    public List<IngameNews> findByChannelInOrderByCreateDateDesc(List<IngameNewsChannel> channels);

    @Query("select n from IngameNews as n, IngameNewsChannel as c where n.channel = c and c.name in :channelNames order by n.createDate desc")
    public List<IngameNews> getNewsInChannels(@Param("channelNames") List<String> channelNames);

    @Query("select max(n.updateDate) from IngameNews as n")
    public Optional<Date> getLastUpdate();

}
