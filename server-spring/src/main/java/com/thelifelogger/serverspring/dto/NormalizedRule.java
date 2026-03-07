package com.thelifelogger.serverspring.dto;

import com.thelifelogger.serverspring.model.ActivityRule;

public record NormalizedRule(String pattern, String category, String domain) {
}
