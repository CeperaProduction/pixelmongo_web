package ru.pixelmongo.pixelmongo.services.impl;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ru.pixelmongo.pixelmongo.model.dao.AdminLogRecord;
import ru.pixelmongo.pixelmongo.model.dao.User;
import ru.pixelmongo.pixelmongo.repositories.AdminLogRecordRepositiory;
import ru.pixelmongo.pixelmongo.services.AdminLogService;

@Service("adminLogService")
public class AdminLogServiceImpl implements AdminLogService{

    @Autowired
    private AdminLogRecordRepositiory logs;

    @Autowired
    @Qualifier("defaultLocale")
    private Locale defaultLocale;

    @Autowired
    private MessageSource msg;

    @Override
    public void log(String langKey, Object[] langData, User user, String ip) {
        log(msg.getMessage(langKey, langData, langKey, defaultLocale), user, ip);
    }

    @Override
    public void log(String data, User user, String ip) {
        AdminLogRecord record = new AdminLogRecord(user, ip, data);
        logs.save(record);
    }

    @Override
    public Page<AdminLogRecord> search(String searchLine, Pageable limits) {
        if(!StringUtils.hasText(searchLine)) {
            return logs.findAllByOrderByDateDesc(limits);
        }
        return logs.search(searchLine, limits);
    }

}
