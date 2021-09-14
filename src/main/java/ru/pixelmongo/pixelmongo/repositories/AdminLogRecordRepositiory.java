package ru.pixelmongo.pixelmongo.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.pixelmongo.pixelmongo.model.entities.AdminLogRecord;

@Repository
public interface AdminLogRecordRepositiory extends JpaRepository<AdminLogRecord, Integer>{

    public Page<AdminLogRecord> findAllByUserIdOrderByDateDesc(int userId, Pageable limits);

    public Page<AdminLogRecord> findAllByUserNameOrderByDateDesc(String userName, Pageable limits);

    public Page<AdminLogRecord> findAllByOrderByDateDesc(Pageable limits);

    public Page<AdminLogRecord> findByDataContainsIgnoreCaseOrUserNameContainsIgnoreCaseOrderByDateDesc(String dataPart, String userNamePart, Pageable limits);

    public default Page<AdminLogRecord> search(String search, Pageable limits) {
        return findByDataContainsIgnoreCaseOrUserNameContainsIgnoreCaseOrderByDateDesc(search, search, limits);
    }

}
