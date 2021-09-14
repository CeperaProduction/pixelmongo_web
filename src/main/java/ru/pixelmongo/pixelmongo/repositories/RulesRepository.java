package ru.pixelmongo.pixelmongo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.pixelmongo.pixelmongo.model.dao.Rules;

@Repository
public interface RulesRepository extends CrudRepository<Rules, Integer>{

}
