package ru.pixelmongo.pixelmongo.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.pixelmongo.pixelmongo.model.entities.UserGroup;

@Repository
public interface UserGroupRepository extends CrudRepository<UserGroup, Integer>{

    public Optional<UserGroup> findByName(String groupName);
    
}
