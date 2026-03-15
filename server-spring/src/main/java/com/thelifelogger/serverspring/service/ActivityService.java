package com.thelifelogger.serverspring.service;

import com.thelifelogger.serverspring.dto.ActivitySummary;
import com.thelifelogger.serverspring.dto.DashboardResponse;
import com.thelifelogger.serverspring.dto.NormalizedRule;
import com.thelifelogger.serverspring.dto.StatsData;
import com.thelifelogger.serverspring.model.ActivityRule;
import com.thelifelogger.serverspring.model.ActivitySession;
import com.thelifelogger.serverspring.model.RuleType;
import com.thelifelogger.serverspring.repository.ActivityRuleRepository;
import com.thelifelogger.serverspring.repository.ActivitySessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
@Service
public class ActivityService {
    private final ActivitySessionRepository activitySessionRepository;
    private final ActivityRuleRepository activityRuleRepository;
    private final Clock clock;

    @Transactional
    public void processPing(String processName, String windowTitle) {
        Optional<ActivitySession> runningSession = activitySessionRepository.findFirstByEndTimeIsNullOrderByStartTimeDesc();

        if(runningSession.isEmpty()){
            createActivitySession(processName, windowTitle);
        }
        else{
            ActivitySession currentSession = runningSession.get();

            String runningProcess = currentSession.getProcessName();
            String runningWindow = currentSession.getWindowTitle();

            if(runningProcess.equals(processName) && runningWindow.equals(windowTitle)) {
                currentSession.setLastSeen(Instant.now(clock));

                log.debug("Heartbeat: {} - {}", processName, windowTitle);
            } else {
                log.info("Switching from {} to {}", runningProcess, processName);

                Duration duration = Duration.between(currentSession.getStartTime(), currentSession.getLastSeen());
                if(duration.toSeconds() >= 5) {
                    currentSession.setDurationSeconds(duration.toSeconds());
                    currentSession.setEndTime(currentSession.getLastSeen());
                    activitySessionRepository.save(currentSession);
                } else {
                    activitySessionRepository.delete(currentSession);
                }

                createActivitySession(processName, windowTitle);
            }
        }
    }

    private void createActivitySession(String processName, String windowTitle) {
        ActivitySession newSession = new ActivitySession();

        Instant now = Instant.now(clock);

        newSession.setProcessName(processName);
        newSession.setWindowTitle(windowTitle);
        newSession.setStartTime(now);
        newSession.setLastSeen(now);

        activitySessionRepository.save(newSession);

        log.info("[NEW SESSION] Started: {}", processName);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void closeAbandonedSession() {

        Instant checkPoint = Instant.now(clock).minusSeconds(15);

        List<ActivitySession> endNullSessions = activitySessionRepository.findAllByEndTimeIsNullAndLastSeenBefore(checkPoint);
        if(!endNullSessions.isEmpty()) {
            log.info("Found {} abandoned sessions. Closing...", endNullSessions.size());

            for(ActivitySession abandonedSession : endNullSessions) {
                abandonedSession.setEndTime(abandonedSession.getLastSeen());
                Duration duration = Duration.between(abandonedSession.getStartTime(), abandonedSession.getLastSeen());
                abandonedSession.setDurationSeconds(duration.toSeconds());
            }
        }
    }

    public void processRule(ActivityRule rule) {
        String pattern = rule.getPattern();
        String category = rule.getCategory();
        String domain = rule.getDomain();

        ActivityRule activityRule = new ActivityRule();

        activityRule.setPattern(pattern);
        activityRule.setCategory(category);
        activityRule.setDomain(domain);

        if(!domain.isEmpty()) activityRule.setRuleType(RuleType.TITLE);
        else activityRule.setRuleType(RuleType.PROCESS);

        activityRuleRepository.save(activityRule);
    }

    public DashboardResponse getSummaryForRange(String range, LocalDate startDate, LocalDate endDate) {

        ZonedDateTime now = ZonedDateTime.now(clock);

        Instant startDateInstant = null;
        Instant endDateInstant = null;
        if(startDate != null) startDateInstant = startDate.atStartOfDay(clock.getZone()).toInstant();
        if(endDate != null) endDateInstant = endDate.atStartOfDay(clock.getZone()).toInstant();


        if(range == null) range = "";

        switch(range) {

            case "daily":
                if(startDateInstant == null) {
                    startDateInstant = LocalDate.now(clock).atStartOfDay(clock.getZone()).toInstant();
                    endDateInstant = Instant.now(clock);
                } else {
                    endDateInstant = startDateInstant.atZone(clock.getZone()).plusDays(1).toInstant();
                }


                break;

            case "weekly":
                if(startDateInstant == null) {
                    startDateInstant = now
                            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                            .truncatedTo(ChronoUnit.DAYS)
                            .toInstant();
                    endDateInstant = Instant.now(clock);
                } else {
                    endDateInstant = startDateInstant.atZone(clock.getZone()).plusWeeks(1).toInstant();
                }
                break;

            case "monthly":
                if(startDateInstant == null) {
                    startDateInstant = now
                            .with(TemporalAdjusters.firstDayOfMonth())
                            .truncatedTo(ChronoUnit.DAYS)
                            .toInstant();
                    endDateInstant = Instant.now(clock);
                } else {
                    endDateInstant = startDateInstant.atZone(clock.getZone()).plusMonths(1).toInstant();
                }

                break;

            case "yearly":
                if(startDateInstant == null) {
                    startDateInstant = now
                            .with(TemporalAdjusters.firstDayOfYear())
                            .truncatedTo(ChronoUnit.DAYS)
                            .toInstant();
                    endDateInstant = Instant.now(clock);
                } else {
                    endDateInstant = startDateInstant.atZone(clock.getZone()).plusYears( 1).toInstant();
                }

                break;

            default:
                if(startDateInstant == null && endDateInstant == null) {
                    startDateInstant = Instant.EPOCH;
                    endDateInstant = Instant.now(clock);
                }
                break;
        }

        return enrich(activitySessionRepository.getSummaryForRange(startDateInstant, endDateInstant));
    }

    private DashboardResponse enrich(List<Object[]> rawData) {
        List<NormalizedRule> processRules = normalizeRules("PROCESS");
        List<NormalizedRule> titleRules = normalizeRules("TITLE");

        Map<String, Long> processMap = new HashMap<>();
        Map<String, Long> categoryMap = new HashMap<>();
        Map<String, Long> browserMap = new HashMap<>();

        StatsData mapsData = fillMaps(rawData, processMap, categoryMap, browserMap, processRules, titleRules);

        List<ActivitySummary> processStats = getStats(mapsData.process(),
                e -> new ActivitySummary(e.getKey(), e.getKey(), e.getValue(), null, null));

        List<ActivitySummary> categoryStats = getStats(mapsData.category(),
                e -> new ActivitySummary(e.getKey(), e.getKey(), e.getValue(), e.getKey(), null));

        List<ActivitySummary> browserStats = getStats(mapsData.browser(),
                e -> new ActivitySummary(null, null, e.getValue(), null, e.getKey()));

        return new DashboardResponse(processStats, categoryStats, browserStats);
    }

    private List<NormalizedRule> normalizeRules(String expectedRuleType) {
        List<ActivityRule> allRules = activityRuleRepository.findAll();
        List<NormalizedRule> normalizedRules = new ArrayList<>();

        for(ActivityRule rule : allRules) {
            String currentPattern = rule.getPattern().replaceAll(" ", "").toLowerCase();

            NormalizedRule currentRule = new NormalizedRule(currentPattern, rule.getCategory(), rule.getDomain());

            String ruleType = String.valueOf(rule.getRuleType());
            if(ruleType.equals(expectedRuleType)) normalizedRules.add(currentRule);
        }

        return normalizedRules;
    }

    private StatsData fillMaps(List<Object[]> rawData, Map<String, Long> processMap, Map<String, Long> categoryMap, Map<String, Long> browserMap, List<NormalizedRule> processRules,  List<NormalizedRule> titleRules) {

        for(Object[] currentData : rawData) {

            String processName = currentData[0] != null ? currentData[0].toString() : "";
            String cleanProcessName = processName.replaceAll(" ", "").toLowerCase();

            String windowName = currentData[1] != null ? currentData[1].toString() : "";
            String cleanWindowName = windowName.replaceAll(" ", "").toLowerCase();

            Long duration = ((Number) currentData[2]).longValue();

            String category = "Uncategorized";
            String domain = "";

            for(NormalizedRule rule : titleRules) {
                if(cleanWindowName.contains(rule.pattern())) {
                    category = rule.category();
                    domain = rule.domain();
                    break;
                }
            }

            if(category.equals("Uncategorized")) {
                for(NormalizedRule rule : processRules) {
                    if(cleanProcessName.contains(rule.pattern())) {
                        category = rule.category();
                        break;
                    }
                }
            }

            processMap.put(processName, processMap.getOrDefault(processName, 0L) + duration);
            categoryMap.put(category, categoryMap.getOrDefault(category, 0L) + duration);
            if(!domain.isEmpty()) {
                browserMap.put(domain, browserMap.getOrDefault(domain, 0L) + duration);
            }
        }

        return new StatsData(processMap, categoryMap, browserMap);
    }

    private List<ActivitySummary> getStats(Map<String, Long> map, Function<Map.Entry<String, Long>, ActivitySummary> mapper) {
        return map.entrySet().stream()
                .map(mapper)
                .sorted(Comparator.comparingLong(ActivitySummary::durationSeconds).reversed())
                .toList();
    }
}
