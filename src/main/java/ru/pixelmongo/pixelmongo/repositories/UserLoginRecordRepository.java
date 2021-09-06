package ru.pixelmongo.pixelmongo.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.entities.User;
import ru.pixelmongo.pixelmongo.model.entities.UserLoginRecord;

public interface UserLoginRecordRepository extends CrudRepository<UserLoginRecord, Integer>{

    public List<UserLoginRecord> findByUser(User user);

}
