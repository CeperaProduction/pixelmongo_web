package ru.pixelmongo.pixelmongo.repositories.primary;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmation;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmationType;

public interface MailedConfirmationRepository extends CrudRepository<MailedConfirmation, Integer>{

    public Optional<MailedConfirmation> findByKey(String key);

    @Transactional
    public void deleteByUserIdAndType(int userId, MailedConfirmationType type);

    public Optional<MailedConfirmation> findTopByUserIdAndTypeOrderByCreateDateDesc(int userId, MailedConfirmationType type);

}
