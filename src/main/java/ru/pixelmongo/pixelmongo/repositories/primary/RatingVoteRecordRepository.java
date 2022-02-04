package ru.pixelmongo.pixelmongo.repositories.primary;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

import ru.pixelmongo.pixelmongo.handlers.RatingHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.RatingVoteRecord;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;

public interface RatingVoteRecordRepository extends CrudRepository<RatingVoteRecord, Integer>{

    //public List<RatingVoteRecord> findByUserIdAndRatingAndDateBetweenOrderByDateDesc(int userId, String rating, Date startDate, Date endDate);

    public boolean existsByUserIdAndRatingAndDateGreaterThanEqual(int userId, String rating, Date date);

    public Page<RatingVoteRecord> findByUserNameContainsIgnoreCaseAndDateBetweenOrderByDateDesc(String userName, Date startDate, Date endDate, Pageable page);

    public Page<RatingVoteRecord> findByUserNameContainsIgnoreCaseAndRatingAndDateBetweenOrderByDateDesc(String userName, String rating, Date startDate, Date endDate, Pageable page);

    /*
    public default List<RatingVoteRecord> getVotes(User user, RatingHandler handler, Date startDate, Date endDate){
        return findByUserIdAndRatingAndDateBetweenOrderByDateDesc(user.getId(), handler.getName(), startDate, endDate);
    }
    */

    public default boolean isVotedAfter(User user, RatingHandler handler, Date date) {
        return existsByUserIdAndRatingAndDateGreaterThanEqual(user.getId(), handler.getName(), date);
    }

    public default Page<RatingVoteRecord> searchVotes(String userName, @Nullable RatingHandler handler, Date startDate, Date endDate, Pageable page){
        if(handler == null)
            return findByUserNameContainsIgnoreCaseAndDateBetweenOrderByDateDesc(userName, startDate, endDate, page);
        return findByUserNameContainsIgnoreCaseAndRatingAndDateBetweenOrderByDateDesc(userName, handler.getName(), startDate, endDate, page);
    }

}
