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

    public Iterable<T> findAllByOrderByOrdinaryAsc();

    @Query("SELECT new "+OD_IMPL+"(e.id, e.ordinary) FROM #{#entityName} e")
    public List<OrderedData<ID>> findAllOrdinaries();

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.ordinary = :#{#n.ordinary} WHERE e.id = :#{#n.id}")
    public void updateOrdinary(@Param("n") OrderedData<ID> data);

    @Query("SELECT max(e.ordinary) FROM #{#entityName} e")
    public int getMaxOrdinary();

}
