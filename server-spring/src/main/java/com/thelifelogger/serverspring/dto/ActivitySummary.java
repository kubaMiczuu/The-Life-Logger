package com.thelifelogger.serverspring.dto;

public record ActivitySummary(String processName, String label , Long durationSeconds, String category, String domain) {}
