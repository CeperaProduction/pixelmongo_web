package ru.pixelmongo.pixelmongo.controllers.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.pixelmongo.pixelmongo.handlers.RatingHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.RatingVoteRecord;
import ru.pixelmongo.pixelmongo.repositories.primary.RatingVoteRecordRepository;
import ru.pixelmongo.pixelmongo.services.RatingService;
import ru.pixelmongo.pixelmongo.services.TemplateService;

@Controller
@RequestMapping("/admin/ratingvotes")
public class RatingVoteLogsController {

    @Autowired
    private TemplateService tpl;

    @Autowired
    private RatingVoteRecordRepository votes;

    @Autowired
    private RatingService ratings;

    @GetMapping
    public String searchLogs(Model model, Locale loc,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "handler", defaultValue = "all") String ratingName,
            @RequestParam(name = "start", defaultValue = "-1") int start,
            @RequestParam(name = "end", defaultValue = "-1") int end,
            @RequestParam(name = "page", defaultValue = "1") int page) {

        if(!StringUtils.hasText(search) && search.trim().length() < 3) {
            search = "";
        }
        if(start == -1 || end == -1) {
            int[] b = getMonthTimeBounds(loc);
            start = b[0];
            end = b[1];
        }

        RatingHandler ratingHandler = ratings.getHandler(ratingName).orElse(null);

        search = search.trim();
        Pageable pageable = PageRequest.of(page-1, 1000);
        Page<RatingVoteRecord> pageVotes = this.votes.searchVotes(search,
                ratingHandler, new Date(1000L*start), new Date(1000L*end), pageable);

        List<RatingVoteRecord> votes = pageVotes.getContent();

        model.addAttribute("search", search);
        model.addAttribute("votes", votes);
        model.addAttribute("votes_count", pageVotes.getTotalElements());
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        List<String> ratingHandlers = this.ratings.getHandlers().stream()
                .filter(RatingHandler::isEnabled).map(RatingHandler::getName)
                .collect(Collectors.toList());
        model.addAttribute("ratingHandlers", ratingHandlers);
        model.addAttribute("handler", ratingName);
        tpl.addPagination(model, page, pageVotes.getTotalPages(), 9);

        return "admin/ratingvotes";
    }

    private int[] getMonthTimeBounds(Locale loc) {
        Calendar cal = new GregorianCalendar(loc);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        int start = (int) (cal.getTimeInMillis()/1000);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        int end = (int) (cal.getTimeInMillis()/1000) + 86400;
        return new int[] {start, end};
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "ratingvotes");
    }

}
