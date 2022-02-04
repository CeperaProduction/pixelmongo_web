package ru.pixelmongo.pixelmongo.repositories.primary;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.StringUtils;

import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.UserGroup;

public interface UserRepository extends JpaRepository<User, Integer>{

    public Optional<User> findByName(String name);

    public Optional<User> findByEmail(String email);

    public Page<User> findByNameContainsIgnoreCase(String namePart, Pageable limits);

    public Page<User> findByNameContainsIgnoreCaseAndGroup(String namePart, UserGroup group, Pageable limits);

    public Page<User> findByGroup(UserGroup group, Pageable limits);

    public Page<User> findAllBy(Pageable limits);

    public default Page<User> findAll(String namePart, UserGroup group, Pageable limits){
        if(group == null)
            return findAll(namePart, limits);
        if(!StringUtils.hasText(namePart))
            return findAll(group, limits);
        return findByNameContainsIgnoreCaseAndGroup(namePart, group, limits);
    }

    public default Page<User> findAll(String namePart, Pageable limits){
        if(!StringUtils.hasText(namePart))
            return this.findAllBy(limits);
        return this.findByNameContainsIgnoreCase(namePart, limits);
    }

    public default Page<User> findAll(UserGroup group, Pageable limits){
        if(group == null)
            return findAllBy(limits);
        return findByGroup(group, limits);
    }

    public default Page<User> findAll(Pageable limits){
        return this.findAllBy(limits);
    }

}
