package ru.pixelmongo.pixelmongo.repositories.primary.donate;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.pixelmongo.pixelmongo.model.dao.OrderedData;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateCategory;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePage;
import ru.pixelmongo.pixelmongo.repositories.OrderedDataRepository;

public interface DonateCategoryRepository extends CrudRepository<DonateCategory, Integer>,
    OrderedDataRepository<DonateCategory, Integer>{

    @Query(QUERY_FIND_ALL_ORDINARIES+" WHERE e.page = :page")
    public List<OrderedData<Integer>> findAllOrdinaries(@Param("page") DonatePage page);

    @Query(QUERY_GET_MAX_ORDINARY+" WHERE e.page = :page")
    public int getMaxOrdinary(@Param("page") DonatePage page);

}
