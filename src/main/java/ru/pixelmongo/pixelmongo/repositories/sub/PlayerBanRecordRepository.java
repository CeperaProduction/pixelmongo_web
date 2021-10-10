package ru.pixelmongo.pixelmongo.repositories.sub;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.util.StringUtils;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.sub.PlayerBanRecord;

public interface PlayerBanRecordRepository extends Repository<PlayerBanRecord, Integer>{

    public void findById(int id);

    public void deleteById(int id);

    @Modifying
    @Transactional
    @Query("delete from PlayerBanRecord r where r.player = :#{#u.name}")
    public void delete(@Param("u") User user);

    public void delete(PlayerBanRecord entity);

    @Deprecated
    @Query("SELECT r FROM PlayerBanRecord r WHERE r.id = :id AND (r.endTime = 0 OR r.endTime > :now)")
    public Optional<PlayerBanRecord> getActiveBan(@Param("id") int id, @Param("now") int currentTimestamp);

    @Deprecated
    @Query("SELECT r FROM PlayerBanRecord r WHERE r.player = :#{#u.name} AND (r.endTime = 0 OR r.endTime > :now)")
    public Optional<PlayerBanRecord> getActiveBan(@Param("u") User user, @Param("now") int currentTimestamp);

    public default Optional<PlayerBanRecord> getActiveBan(User user){
        return getActiveBan(user, (int) (System.currentTimeMillis()/1000));
    }

    public default Optional<PlayerBanRecord> getActiveBan(int id){
        return getActiveBan(id, (int) (System.currentTimeMillis()/1000));
    }

    @Deprecated
    public Page<PlayerBanRecord> findAllByEndTimeOrEndTimeGreaterThanOrderByTimeDesc(int zero, int currentTimestamp, Pageable page);

    public default Page<PlayerBanRecord> getActiveBans(Pageable page){
        return findAllByEndTimeOrEndTimeGreaterThanOrderByTimeDesc(0, (int) (System.currentTimeMillis()/1000), page);
    }

    @Deprecated
    @Query("SELECT r FROM PlayerBanRecord r WHERE (r.endTime = 0 OR r.endTime > :now) AND r.player LIKE %:pnp% ORDER BY r.time DESC")
    public Page<PlayerBanRecord> searchActiveBans(@Param("pnp") String playerNamePart, @Param("now") int currentTimestamp, Pageable page);

    public default Page<PlayerBanRecord> getActiveBans(String playerNameSearch, Pageable page){
        if(StringUtils.hasText(playerNameSearch))
            return searchActiveBans(playerNameSearch, (int) (System.currentTimeMillis()/1000), page);
        return getActiveBans(page);
    }

}
