package com.thelifelogger.serverspring.dto;

import java.util.List;

public record DashboardResponse(
        List<ActivitySummary> processStats,
        List<ActivitySummary> categoryStats,
        List<ActivitySummary> browserStats
) {}
