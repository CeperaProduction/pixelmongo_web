package ru.pixelmongo.pixelmongo.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.pixelmongo.pixelmongo.model.dao.UserPermission;

@Repository
public interface UserPermissionRepository extends CrudRepository<UserPermission, Integer>{

    public Optional<UserPermission> findByValue(String permissionValue);

}
