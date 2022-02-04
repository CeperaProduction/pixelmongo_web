package ru.pixelmongo.pixelmongo.controllers.open;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.pixelmongo.pixelmongo.exceptions.RatingHandlerDisabledException;
import ru.pixelmongo.pixelmongo.exceptions.RatingHandlerNotFoundException;
import ru.pixelmongo.pixelmongo.handlers.RatingHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.services.RatingService;

@RestController
@RequestMapping("/open/ratings")
public class RatingsGatewayController {

    @Autowired
    private RatingService ratings;

    @Autowired
    private UserRepository users;


    @RequestMapping(value = "/{handler}", method = {RequestMethod.GET, RequestMethod.POST})
    public Object handleWebHook(@PathVariable("handler") String handlerName,
            @RequestParam Map<String, String> allParams, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        RatingHandler handler = ratings.getHandler(handlerName)
                .orElseThrow(()->new RatingHandlerNotFoundException(handlerName));
        if(!handler.isEnabled()) throw new RatingHandlerDisabledException(handlerName);

        response.setContentType(handler.useJson() ? "application/json" : "text/plain");

        if(!handler.isValidRequest(allParams, loc, request)) {
            RatingService.LOGGER.warn("Wrong vote notification request. Handler: "+handlerName+" params: "+joinParams(allParams));
            return handler.getInvalidRequestResult(allParams, loc, request, response);
        }
        String userName = handler.getUserName(allParams, loc, request);
        User user = userName == null ? null : users.findByName(userName).orElse(null);
        if(user == null) {
            RatingService.LOGGER.warn(userName+" voted but is not found");
            return handler.getUserNotFoundResult(userName, allParams, loc, request, response);
        }

        synchronized(handler) {
            if(ratings.isOncePerDay() && ratings.isVotedToday(user, handler)) {
                RatingService.LOGGER.warn(user.getName()+" voted at least twice in rating "+handler.getName());
                return handler.getAlreadyVotedResult(user, allParams, loc, request, response);
            }
            user.changeBalance(ratings.getVoteReward());
            users.save(user);
            ratings.markVoted(user, handler);
        }

        RatingService.LOGGER.info(user.getName()+" voted in rating "+handler.getName()+" and received revard");

        return handler.getSuccessResult(user, allParams, loc, request, response);
    }

    private String joinParams(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        boolean a = false;
        for(Entry<String, String> ent : params.entrySet()) {
            if(a) {
               sb.append(", ");
            }else a = true;
            sb.append(ent.getKey()).append('=').append(ent.getValue());
        }
        return sb.toString();
    }

}
