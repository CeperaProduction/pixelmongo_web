package ru.pixelmongo.pixelmongo.repositories.primary;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.Staff;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.repositories.OrderedDataRepository;

public interface StaffRepository extends CrudRepository<Staff, Integer>,
    OrderedDataRepository<Staff, Integer>{

    public Optional<Staff> findByUser(User user);

    public void deleteByUser(User user);

}
