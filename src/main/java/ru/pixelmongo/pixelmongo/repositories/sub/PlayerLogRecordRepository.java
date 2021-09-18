package ru.pixelmongo.pixelmongo.repositories.sub;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import ru.pixelmongo.pixelmongo.model.dao.sub.PlayerLogRecord;

public interface PlayerLogRecordRepository extends Repository<PlayerLogRecord, Integer>{

    @Query("SELECT DISTINCT r.server FROM PlayerLogRecord r")
    public List<String> findServers();

    @Query("SELECT DISTINCT r.player FROM PlayerLogRecord r WHERE r.server = :server")
    public List<String> findPlayers(@Param("server") String server);

    public Page<PlayerLogRecord> findByServerAndPlayerAndTimestampGreaterThanEqualAndTimestampLessThanEqualOrderByTimestampAsc(
            String server, String player, int start, int end, Pageable page);

    public Page<PlayerLogRecord> findByDataContainsIgnoreCaseAndTimestampGreaterThanEqualAndTimestampLessThanEqualOrderByTimestampAsc(
            String search, int start, int end, Pageable page);

    public default Page<PlayerLogRecord> findLogs(String server, String player,
            int start, int end, Pageable page){
        return findByServerAndPlayerAndTimestampGreaterThanEqualAndTimestampLessThanEqualOrderByTimestampAsc(
                server, player, start, end, page);
    }

    public default Page<PlayerLogRecord> findLogs(String search, int start, int end, Pageable page){
        return findByDataContainsIgnoreCaseAndTimestampGreaterThanEqualAndTimestampLessThanEqualOrderByTimestampAsc(
                search, start, end, page);
    }

    public default Page<PlayerLogRecord> findLogs(String server, String player,
            Date start, Date end, Pageable page){
        int startInt = (int) (start.getTime()/1000);
        int endInt = (int) (end.getTime()/1000);
        return findLogs(server, player, startInt, endInt, page);
    }

    public default Page<PlayerLogRecord> findLogs(String search, Date start, Date end, Pageable page){
        int startInt = (int) (start.getTime()/1000);
        int endInt = (int) (end.getTime()/1000);
        return findLogs(search, startInt, endInt, page);
    }

}
