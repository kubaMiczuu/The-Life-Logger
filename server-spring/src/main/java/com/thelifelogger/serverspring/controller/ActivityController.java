package com.thelifelogger.serverspring.controller;

import com.thelifelogger.serverspring.dto.ActivitySummary;
import com.thelifelogger.serverspring.dto.PingRequest;
import com.thelifelogger.serverspring.repository.ActivitySessionRepository;
import com.thelifelogger.serverspring.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping("/activities/ping")
    public void postPing(@RequestBody PingRequest request) {
        activityService.processPing(request.processName(),  request.windowTitle());
    }

    @GetMapping("/stats/summary")
    public List<ActivitySummary> getSummary() {
        return activityService.getSummary();
    }

    @GetMapping("/stats/summary/custom")
    public List<ActivitySummary> getCustomSummary() {
        return activityService.getCustomSummary();
    }

    @GetMapping("/stats/summary/daily")
    public List<ActivitySummary> getDailySummary() {
        return activityService.getDailySummary();
    }

    @GetMapping("/stats/summary/weekly")
    public List<ActivitySummary> getWeeklySummary() {
        return activityService.getWeeklySummary();
    }

    @GetMapping("/stats/summary/monthly")
    public List<ActivitySummary> getMonthlySummary() {
        return activityService.getMonthlySummary();
    }

    @GetMapping("/stats/summary/last7days")
    public List<ActivitySummary> getLast7DaysSummary() {
        return activityService.getLast7DaysSummary();
    }

    @GetMapping("/stats/summary/last30days")
    public List<ActivitySummary> getLast30DaysSummary() {
        return activityService.getLast30DaysSummary();
    }
}
