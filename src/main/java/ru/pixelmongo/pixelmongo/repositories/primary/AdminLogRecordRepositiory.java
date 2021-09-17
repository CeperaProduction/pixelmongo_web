package ru.pixelmongo.pixelmongo.repositories.primary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.AdminLogRecord;

public interface AdminLogRecordRepositiory extends JpaRepository<AdminLogRecord, Integer>{

    public Page<AdminLogRecord> findAllByUserIdOrderByDateDesc(int userId, Pageable limits);

    public Page<AdminLogRecord> findAllByUserNameOrderByDateDesc(String userName, Pageable limits);

    public Page<AdminLogRecord> findAllByOrderByDateDesc(Pageable limits);

    public Page<AdminLogRecord> findByDataContainsIgnoreCaseOrUserNameContainsIgnoreCaseOrderByDateDesc(String dataPart, String userNamePart, Pageable limits);

    public default Page<AdminLogRecord> search(String search, Pageable limits) {
        return findByDataContainsIgnoreCaseOrUserNameContainsIgnoreCaseOrderByDateDesc(search, search, limits);
    }

}
