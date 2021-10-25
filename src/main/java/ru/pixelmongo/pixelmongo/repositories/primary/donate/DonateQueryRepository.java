package ru.pixelmongo.pixelmongo.repositories.primary.donate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateQuery;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateServer;

public interface DonateQueryRepository extends CrudRepository<DonateQuery, Integer>{

    public List<DonateQuery> findAllByServer(String serverConfigName);

    @Deprecated
    public List<DonateQuery> findAllByServerAndExecuteAfterLessThanEqualAndDoneFalse(String serverConfigName, int currentTimestamp);

    @Deprecated
    public Page<DonateQuery> findByServerAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
            String server, int start, int end, Pageable page);

    @Deprecated
    public Optional<DonateQuery> findByServerAndPlayerAndPackIdAndBackOfNotNullAndDoneFalse(String serverConfigName, String playerName, int packId);

    public default Page<DonateQuery> getQueries(DonateServer server, int start, int end, Pageable page) {
        return findByServerAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(server.getConfigName(), start, end, page);
    }

    public default List<DonateQuery> getCurrentActiveQueries(DonateServer server){
        return findAllByServerAndExecuteAfterLessThanEqualAndDoneFalse(server.getConfigName(), (int) (System.currentTimeMillis()/1000));
    }

    public default Optional<DonateQuery> getActiveBackQuery(DonateServer server, User user, DonatePack pack){
        return findByServerAndPlayerAndPackIdAndBackOfNotNullAndDoneFalse(server.getConfigName(), user.getName(), pack.getId());
    }

    @Query("SELECT DISTINCT sum(q.spentMoney) FROM DonateQuery q WHERE q.server = :#{#server.configName} AND q.date >= :start AND q.date <= :end")
    public int getSpentMoney(@Param("server") DonateServer server, @Param("start") int start, @Param("end") int end);



}
