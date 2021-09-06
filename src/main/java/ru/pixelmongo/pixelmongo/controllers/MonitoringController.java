package ru.pixelmongo.pixelmongo.controllers;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.pixelmongo.pixelmongo.model.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.services.MonitoringService;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    @Autowired
    private MonitoringService monitoring;

    @Autowired
    private DateFormatter df;

    @GetMapping
    public ResultDataMessage<List<MonitoringService.MonitoringResult>> monitoring() {
        return new ResultDataMessage<>(DefaultResult.OK,
                "Last update: "+df.print(new Date(monitoring.getLastUpdateTime()), Locale.ROOT),
                monitoring.getMonitoringList());
    }

}
