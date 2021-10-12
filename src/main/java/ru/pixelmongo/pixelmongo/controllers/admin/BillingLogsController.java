package ru.pixelmongo.pixelmongo.controllers.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

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

import ru.pixelmongo.pixelmongo.model.dao.primary.BillingData;
import ru.pixelmongo.pixelmongo.repositories.primary.BillingDataRepository;
import ru.pixelmongo.pixelmongo.services.TemplateService;

@Controller
@RequestMapping("/admin/billing")
public class BillingLogsController {

    @Autowired
    private TemplateService tpl;

    @Autowired
    private BillingDataRepository payments;

    @GetMapping
    public String searchLogs(Model model, Locale loc,
            @RequestParam(name = "search", defaultValue = "") String search,
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

        search = search.trim();
        Pageable pageable = PageRequest.of(page-1, 1000);
        Page<BillingData> pagePayments = this.payments.searchPayments(search,
                new Date(1000L*start), new Date(1000L*end), pageable);

        float sum = 0;
        List<BillingData> payments = pagePayments.getContent();
        for(BillingData payment : payments)
            sum += payment.getProfit();

        model.addAttribute("search", search);
        model.addAttribute("payments", payments);
        model.addAttribute("payments_count", pagePayments.getTotalElements());
        model.addAttribute("profit", String.format("%1.2f", sum));
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        tpl.addPagination(model, page, pagePayments.getTotalPages(), 9);

        return "admin/billing";
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
        model.addAttribute("mode", "billing");
    }

}
