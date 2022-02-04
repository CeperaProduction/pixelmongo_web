package ru.pixelmongo.pixelmongo.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import ru.pixelmongo.pixelmongo.handlers.RatingHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.RatingVoteRecord;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.repositories.primary.RatingVoteRecordRepository;
import ru.pixelmongo.pixelmongo.services.RatingService;
import ru.pixelmongo.pixelmongo.services.TimeService;

public class RatingServiceImpl implements RatingService{

    @Autowired
    private RatingVoteRecordRepository votes;

    @Autowired
    private TimeService timeService;

    private Map<String, RatingHandler> ratings = new HashMap<>();

    private final int voteReward;
    private final boolean oncePerDay;

    public RatingServiceImpl(int voteReward, boolean onvePerDay) {
        this.voteReward = voteReward;
        this.oncePerDay = onvePerDay;
    }

    @Autowired
    public void setupHandlers(List<RatingHandler> handlers) {
        handlers.forEach(h->ratings.put(h.getName(), h));

    }

    @Override
    public int getVoteReward() {
        return voteReward;
    }

    @Override
    public boolean isOncePerDay() {
        return oncePerDay;
    }

    @Override
    public Optional<RatingHandler> getHandler(String handlerName){
        return Optional.ofNullable(ratings.get(handlerName));
    }

    @Override
    public List<String> getHandlerNames(){
        return Collections.unmodifiableList(new ArrayList<>(ratings.keySet()));
    }

    @Override
    public List<RatingHandler> getHandlers(){
        return Collections.unmodifiableList(new ArrayList<>(ratings.values()));
    }

    @Override
    public boolean isVotedToday(User user, RatingHandler handler) {
        Date dayStart = timeService.getDayStart();
        return votes.isVotedAfter(user, handler, dayStart);
    }

    @Override
    public void markVoted(User user, RatingHandler handler) {
        RatingVoteRecord vote = new RatingVoteRecord(handler, user, voteReward);
        votes.save(vote);
    }

}
