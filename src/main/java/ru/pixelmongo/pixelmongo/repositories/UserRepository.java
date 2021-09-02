package ru.pixelmongo.pixelmongo.repositories;

import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.data.User;

public interface UserRepository extends CrudRepository<User, Integer>{
	
	public User findByName(String name);
	
	public User findByEmail(String email);

}
