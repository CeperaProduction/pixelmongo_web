package ru.pixelmongo.pixelmongo.repositories.primary;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.UserPermission;

public interface UserPermissionRepository extends CrudRepository<UserPermission, Integer>{

    public Optional<UserPermission> findByValue(String permissionValue);

    public List<UserPermission> findAllByOrderByValueAsc();

    public default List<UserPermission> findAllSorted(){
        return findAllByOrderByValueAsc();
    }

}
