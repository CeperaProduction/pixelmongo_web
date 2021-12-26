package ru.pixelmongo.pixelmongo.controllers.admin.donate;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateQuery;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateServer;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateQueryRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateServerRepository;
import ru.pixelmongo.pixelmongo.services.TemplateService;

@Controller
@RequestMapping("/admin/donate/query")
public class DonateQueryController {

    @Autowired
    private DonateQueryRepository queries;

    @Autowired
    private DonateServerRepository servers;

    @Autowired
    private MessageSource msg;

    @Autowired
    private TemplateService templateService;

    @GetMapping
    public String choose(Model model, Locale loc) {
        int[] mtb = getMonthTimeBounds(loc);
        model.addAttribute("start", mtb[0]);
        model.addAttribute("end", mtb[1]);
        model.addAttribute("server", new DonateServer());
        model.addAttribute("servers", servers.findAll());
        model.addAttribute("spent_money", 0);
        model.addAttribute("spent_bonus", 0);
        model.addAttribute("queries", Collections.emptyList());
        return "admin/donate/queries";
    }

    @GetMapping(params = {"server", "start", "end"})
    public String logs(@RequestParam(name = "server") int serverId,
            @RequestParam(name = "start") int start,
            @RequestParam(name = "end") int end,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model,
            Locale loc) {

        DonateServer server = servers.findById(serverId).orElseThrow(()->serverNotFound(loc));

        Pageable pageable = PageRequest.of(page-1, 1000);
        Page<DonateQuery> queryPage = queries.getQueries(server, start, end, pageable);
        int spentMoney = queries.getSpentMoney(server, start, end);
        int spentBonus = queries.getSpentBonus(server, start, end);

        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("server", server);
        model.addAttribute("servers", servers.findAll());
        model.addAttribute("spent_money", spentMoney);
        model.addAttribute("spent_bonus", spentBonus);
        model.addAttribute("queries", queryPage.getContent());

        templateService.addPagination(model, page, queryPage.getTotalPages(), 9);

        return "admin/donate/queries";
    }

    private ResponseStatusException serverNotFound(Locale loc) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                msg.getMessage("error.status.404.donate_server", null, loc));
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

    //OTHER

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "donate");
    }

}
