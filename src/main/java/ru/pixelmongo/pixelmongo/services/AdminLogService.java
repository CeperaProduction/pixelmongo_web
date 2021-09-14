package ru.pixelmongo.pixelmongo.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.pixelmongo.pixelmongo.model.dao.AdminLogRecord;
import ru.pixelmongo.pixelmongo.model.dao.User;

public interface AdminLogService {

    public void log(String langKey, Object[] langData, User user, String ip);

    public void log(String data, User user, String ip);

    public Page<AdminLogRecord> search(String searchLine, Pageable limits);

}
