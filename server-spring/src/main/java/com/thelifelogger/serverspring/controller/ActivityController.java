package com.thelifelogger.serverspring.controller;

import com.thelifelogger.serverspring.dto.PingRequest;
import com.thelifelogger.serverspring.repository.ActivitySessionRepository;
import com.thelifelogger.serverspring.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping("/ping")
    public void postPing(@RequestBody PingRequest request) {
        activityService.processPing(request.processName(),  request.windowTitle());
    }
}
