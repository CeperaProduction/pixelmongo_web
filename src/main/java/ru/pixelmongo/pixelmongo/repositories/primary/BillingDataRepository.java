package ru.pixelmongo.pixelmongo.repositories.primary;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.StringUtils;

import ru.pixelmongo.pixelmongo.model.dao.primary.BillingData;

public interface BillingDataRepository extends CrudRepository<BillingData, Integer>{

    public Optional<BillingData> findByBillingId(long billingId);

    public Page<BillingData> findByStatusAndUpdatedBetweenOrderByUpdatedDesc(int status, Date start, Date ent, Pageable page);

    public Page<BillingData> findByStatusAndUserNameContainsIgnoreCaseAndUpdatedBetweenOrderByUpdatedDesc(int status, String userNamePart, Date start, Date ent, Pageable page);

    public default Page<BillingData> searchPayments(String userNamePart, Date start, Date end, Pageable page){
        if(StringUtils.hasText(userNamePart)) {
            return findByStatusAndUserNameContainsIgnoreCaseAndUpdatedBetweenOrderByUpdatedDesc(BillingData.STATUS_DONE, userNamePart, start, end, page);
        }
        return findByStatusAndUpdatedBetweenOrderByUpdatedDesc(BillingData.STATUS_DONE, start, end, page);
    }

}
