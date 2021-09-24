package ru.pixelmongo.pixelmongo.repositories.primary.donate;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.pixelmongo.pixelmongo.model.dao.OrderedData;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateCategory;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePage;
import ru.pixelmongo.pixelmongo.repositories.OrderedDataRepository;

public interface DonatePackRepository extends CrudRepository<DonatePack, Integer>,
    OrderedDataRepository<DonatePack, Integer>{

    @Query(QUERY_FIND_ALL_ORDINARIES+" WHERE e.category = :category")
    public List<OrderedData<Integer>> findAllOrdinaries(@Param("category") DonateCategory category);

    @Query(QUERY_GET_MAX_ORDINARY+" WHERE e.category = :category")
    public int getMaxOrdinary(@Param("category") DonateCategory category);

    @Transactional
    @Modifying
    @Query("UPDATE DonatePack p SET p.discount = :discount WHERE p.category = :category")
    public void setDiscount(@Param("category") DonateCategory category, @Param("discount") byte discount);

    @Transactional
    @Modifying
    @Query("UPDATE DonatePack p SET p.discount = :discount WHERE p.category IN (SELECT c.id FROM DonateCategory c WHERE c.page = :page)")
    public void setDiscount(@Param("page") DonatePage page, @Param("discount") byte discount);

    @Transactional
    @Modifying
    @Query("UPDATE DonatePack p SET p.discount = :discount")
    public void setDiscount(@Param("discount") byte discount);

}
