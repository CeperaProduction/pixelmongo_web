package ru.pixelmongo.pixelmongo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.pixelmongo.pixelmongo.services.MonitoringService;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {
    
    @Autowired
    private MonitoringService monitoring;
    
    @GetMapping
    public List<MonitoringService.MonitoringResult> pingTest() {
        return monitoring.getMonitoringList();
    }

}
