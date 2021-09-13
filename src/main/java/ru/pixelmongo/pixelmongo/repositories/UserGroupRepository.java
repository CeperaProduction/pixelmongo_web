package ru.pixelmongo.pixelmongo.repositories;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.pixelmongo.pixelmongo.model.entities.UserGroup;

@Repository
public interface UserGroupRepository extends CrudRepository<UserGroup, Integer>{

    public static final int GROUP_ID_ADMIN = 1;
    public static final int GROUP_ID_USER = 2;

    public Optional<UserGroup> findByName(String groupName);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE users u SET u.group = "+GROUP_ID_USER+" WHERE u.group = ?1")
    public void freeGroup(UserGroup group);

}
