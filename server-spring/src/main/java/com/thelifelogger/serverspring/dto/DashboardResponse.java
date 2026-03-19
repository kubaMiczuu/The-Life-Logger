package com.thelifelogger.serverspring.dto;

import java.util.List;
import java.util.Map;

public record DashboardResponse(
        List<ActivitySummary> processStats,
        List<ActivitySummary> categoryStats,
        List<ActivitySummary> browserStats,
        Map<Integer, Long> timeStats
) {}
