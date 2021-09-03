package ru.pixelmongo.pixelmongo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.pixelmongo.pixelmongo.data.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{
	
	public User findByName(String name);
	
	public User findByEmail(String email);

}
