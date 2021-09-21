package ru.pixelmongo.pixelmongo.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import ru.pixelmongo.pixelmongo.model.dao.OrderedData;

public interface OrderedDataRepository<T extends OrderedData<ID>, ID> extends Repository<T, ID>{

    public static final String OD_IMPL = "ru.pixelmongo.pixelmongo.model.dao.OrderedDataImpl";

    /**
     * Current entity table is mapped as <b>e</b>
     */
    public static final String QUERY_FIND_ALL_ORDINARIES = "SELECT new "+OD_IMPL+"(e.id, e.ordinary) FROM #{#entityName} e";

    /**
     * Current entity table is mapped as <b>e</b>
     */
    public static final String QUERY_GET_MAX_ORDINARY = "SELECT coalesce(max(e.ordinary), 0) FROM #{#entityName} e";

    public Iterable<T> findAllByOrderByOrdinaryAsc();

    @Query(QUERY_FIND_ALL_ORDINARIES)
    public List<OrderedData<ID>> findAllOrdinaries();

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.ordinary = :#{#n.ordinary} WHERE e.id = :#{#n.id}")
    public void updateOrdinary(@Param("n") OrderedData<ID> data);

    @Query(QUERY_GET_MAX_ORDINARY)
    public int getMaxOrdinary();

}
