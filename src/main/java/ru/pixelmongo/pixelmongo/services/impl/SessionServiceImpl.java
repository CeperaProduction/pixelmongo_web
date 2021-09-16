package ru.pixelmongo.pixelmongo.services.impl;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import ru.pixelmongo.pixelmongo.model.UserDetails;
import ru.pixelmongo.pixelmongo.model.dao.User;
import ru.pixelmongo.pixelmongo.model.dao.UserGroup;
import ru.pixelmongo.pixelmongo.services.SessionService;

@Service("sessionService")
public class SessionServiceImpl implements SessionService{

    private static final Logger LOGGER = LogManager.getLogger(SessionService.class);

    @Autowired
    private SessionRegistry sessions;

    @Override
    public void dropSessions(UserGroup group) {
        List<Object> all = sessions.getAllPrincipals();
        LOGGER.debug("Session drop process for group '"+group.getName()+"' started.");
        LOGGER.debug("There are "+all.size()+" principals");
        long count = 0;
        long start = System.currentTimeMillis();
        for(Object o : all) {
            if(o instanceof UserDetails) {
                UserDetails ud = (UserDetails) o;
                if(ud.getGroupId() == group.getId()) {
                    List<SessionInformation> ss = sessions.getAllSessions(ud, false);
                    for(SessionInformation si : ss) {
                        si.expireNow();
                        ++count;
                    }
                }
            }
        }
        long time = System.currentTimeMillis()-start;
        LOGGER.debug(String.format("Dropped "+count+" sessions for %1.3f seconds", time/1000f));
    }

    @Override
    public void dropSessions(User user) {
        dropSessions(new UserDetails(user, Collections.emptyList()));
    }

    @Override
    public void dropSessions(UserDetails userDetails) {
        List<SessionInformation> ss = sessions.getAllSessions(userDetails, false);
        for(SessionInformation si : ss) {
            si.expireNow();
        }
    }

}
