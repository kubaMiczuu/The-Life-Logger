package com.thelifelogger.serverspring.controller;

import com.thelifelogger.serverspring.dto.DashboardResponse;
import com.thelifelogger.serverspring.dto.PingRequest;
import com.thelifelogger.serverspring.model.ActivityRule;
import com.thelifelogger.serverspring.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping("/activities/ping")
    public void postPing(@RequestBody PingRequest request) {
        activityService.processPing(request.processName(),  request.windowTitle());
    }

    @PostMapping("/activities/rule")
    public void postRule(@RequestBody ActivityRule rule) {
        activityService.processRule(rule);
    }

    @GetMapping("/stats/summary")
    public DashboardResponse getSummary(
            @RequestParam(required = false) String range,
            @RequestParam(required = false) @DateTimeFormat LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat LocalDate endDate
    ) {
        return activityService.getSummaryForRange(range, startDate, endDate);
    }
}
