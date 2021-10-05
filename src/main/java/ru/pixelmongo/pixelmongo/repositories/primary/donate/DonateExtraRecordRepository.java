package ru.pixelmongo.pixelmongo.repositories.primary.donate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateExtraRecord;

public interface DonateExtraRecordRepository extends JpaRepository<DonateExtraRecord, Integer>{

    public Page<DonateExtraRecord> findAllByUserNameOrderByDateDesc(String userName, Pageable limits);

    public Page<DonateExtraRecord> findAllByOrderByDateDesc(Pageable limits);

    public Page<DonateExtraRecord> findByDataContainsIgnoreCaseOrUserNameContainsIgnoreCaseOrderByDateDesc(String dataPart, String userNamePart, Pageable limits);

    public default Page<DonateExtraRecord> search(String search, Pageable limits) {
        return findByDataContainsIgnoreCaseOrUserNameContainsIgnoreCaseOrderByDateDesc(search, search, limits);
    }

}
