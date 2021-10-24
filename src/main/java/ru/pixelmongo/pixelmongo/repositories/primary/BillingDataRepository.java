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

    public Page<BillingData> findByStatusAndHandlerAndUpdatedBetweenOrderByUpdatedDesc(int status, String handler, Date start, Date ent, Pageable page);

    public Page<BillingData> findByStatusAndUserNameContainsIgnoreCaseAndUpdatedBetweenOrderByUpdatedDesc(int status, String userNamePart, Date start, Date ent, Pageable page);

    public Page<BillingData> findByStatusAndHandlerAndUserNameContainsIgnoreCaseAndUpdatedBetweenOrderByUpdatedDesc(int status, String userNamePart, String handler, Date start, Date ent, Pageable page);

    public default Page<BillingData> searchPayments(String userNamePart, String handler, Date start, Date end, Pageable page){
        boolean handlerSet = StringUtils.hasText(handler) && !"all".equals(handler);
        if(StringUtils.hasText(userNamePart)) {
            if(!handlerSet)
                return findByStatusAndUserNameContainsIgnoreCaseAndUpdatedBetweenOrderByUpdatedDesc(BillingData.STATUS_DONE, userNamePart, start, end, page);
            return findByStatusAndHandlerAndUserNameContainsIgnoreCaseAndUpdatedBetweenOrderByUpdatedDesc(BillingData.STATUS_DONE, userNamePart, handler, start, end, page);
        }
        if(!handlerSet)
            return findByStatusAndUpdatedBetweenOrderByUpdatedDesc(BillingData.STATUS_DONE, start, end, page);
        return findByStatusAndHandlerAndUpdatedBetweenOrderByUpdatedDesc(BillingData.STATUS_DONE, handler, start, end, page);
    }

}
