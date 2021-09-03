package ru.pixelmongo.pixelmongo.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.pixelmongo.pixelmongo.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{
    
    public Optional<User> findByName(String name);
    
    public Optional<User> findByEmail(String email);

}
