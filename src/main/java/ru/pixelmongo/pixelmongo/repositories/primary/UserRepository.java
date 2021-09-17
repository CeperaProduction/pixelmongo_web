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

    public Page<User> findByNameContainsIgnoreCaseOrderByNameAsc(String namePart, Pageable limits);

    public Page<User> findByNameContainsIgnoreCaseAndGroupOrderByNameAsc(String namePart, UserGroup group, Pageable limits);

    public Page<User> findByGroupOrderByNameAsc(UserGroup group, Pageable limits);

    public Page<User> findAllByOrderByNameAsc(Pageable limits);

    public default Page<User> findAllSorted(String namePart, UserGroup group, Pageable limits){
        if(group == null)
            return findAllSorted(namePart, limits);
        if(!StringUtils.hasText(namePart))
            return findAllSorted(group, limits);
        return findByNameContainsIgnoreCaseAndGroupOrderByNameAsc(namePart, group, limits);
    }

    public default Page<User> findAllSorted(String namePart, Pageable limits){
        if(!StringUtils.hasText(namePart))
            return this.findAllByOrderByNameAsc(limits);
        return this.findByNameContainsIgnoreCaseOrderByNameAsc(namePart, limits);
    }

    public default Page<User> findAllSorted(UserGroup group, Pageable limits){
        if(group == null)
            return findAllByOrderByNameAsc(limits);
        return findByGroupOrderByNameAsc(group, limits);
    }

    public default Page<User> findAllSorted(Pageable limits){
        return this.findAllByOrderByNameAsc(limits);
    }

}
