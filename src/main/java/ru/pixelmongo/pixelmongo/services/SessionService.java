package ru.pixelmongo.pixelmongo.services;

import ru.pixelmongo.pixelmongo.model.UserDetails;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.UserGroup;

public interface SessionService {

    public void dropSessions(UserGroup group);

    public void dropSessions(UserDetails userDetails);

    public void dropSessions(User user);

}
