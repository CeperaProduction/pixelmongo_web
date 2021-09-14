package ru.pixelmongo.pixelmongo.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.pixelmongo.pixelmongo.model.dao.AdminLogRecord;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.TemplateService;

@Controller
@RequestMapping("/admin/logs")
public class AdminLogsController {

    @Autowired
    private AdminLogService logs;

    @Autowired
    private TemplateService templateService;

    @GetMapping
    private String logs(@RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        Page<AdminLogRecord> currentPage = logs.search(search, PageRequest.of(page-1, 50));
        model.addAttribute("logs_count", currentPage.getTotalElements());
        model.addAttribute("logs", currentPage.getContent());
        model.addAttribute("search", search);
        templateService.addPagination(model, page, currentPage.getTotalPages(), 9);
        return "admin/logs";
    }

}
