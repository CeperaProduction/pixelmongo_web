package ru.pixelmongo.pixelmongo.repositories.primary;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import ru.pixelmongo.pixelmongo.model.dao.primary.UserPermission;

public interface UserPermissionRepository extends Repository<UserPermission, Integer>{

    public List<UserPermission> findAll();

    public Optional<UserPermission> findByValue(String permissionValue);

    public List<UserPermission> findAllByOrderByValueAsc();

    public default List<UserPermission> findAllSorted(){
        return findAllByOrderByValueAsc();
    }

}
