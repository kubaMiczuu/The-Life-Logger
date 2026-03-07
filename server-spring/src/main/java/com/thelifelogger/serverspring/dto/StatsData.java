package com.thelifelogger.serverspring.dto;

import java.util.Map;

public record StatsData(
        Map<String, Long> process,
        Map<String, Long> category,
        Map<String, Long> browser
) {
}
