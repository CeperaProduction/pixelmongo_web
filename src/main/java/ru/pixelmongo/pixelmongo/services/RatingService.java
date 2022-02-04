package ru.pixelmongo.pixelmongo.services;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.pixelmongo.pixelmongo.handlers.RatingHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;

public interface RatingService {

    public static final Logger LOGGER = LogManager.getLogger(RatingService.class);

    public Optional<RatingHandler> getHandler(String handlerName);

    public List<String> getHandlerNames();

    public List<RatingHandler> getHandlers();

    public boolean isVotedToday(User user, RatingHandler handler);

    public void markVoted(User user, RatingHandler handler);

    public int getVoteReward();

    public boolean isOncePerDay();

}
