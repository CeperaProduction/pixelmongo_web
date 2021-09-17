package ru.pixelmongo.pixelmongo.repositories.primary;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.UserLoginRecord;

public interface UserLoginRecordRepository extends CrudRepository<UserLoginRecord, Integer>{

    public List<UserLoginRecord> findByUser(User user);

}
