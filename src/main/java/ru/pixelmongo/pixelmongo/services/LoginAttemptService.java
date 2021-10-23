package ru.pixelmongo.pixelmongo.services;

import javax.servlet.http.HttpServletRequest;

public interface LoginAttemptService {

    public boolean isBlocked(HttpServletRequest request);

    public void cleanAttempts(HttpServletRequest request);

    public void onLoginFail(HttpServletRequest request);

}
