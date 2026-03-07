package com.thelifelogger.serverspring.service;

import com.thelifelogger.serverspring.dto.ActivitySummary;
import com.thelifelogger.serverspring.dto.NormalizedRule;
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

@RequiredArgsConstructor
@Slf4j
@Service
public class ActivityService {
    private final ActivitySessionRepository activitySessionRepository;
    private final ActivityRuleRepository activityRuleRepository;

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
                currentSession.setLastSeen(Instant.now());

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

        Instant now = Instant.now();

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

        Instant checkPoint = Instant.now().minusSeconds(15);

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

    public List<ActivitySummary> getSummary() {
        return enrich(activitySessionRepository.getSummary());
    }

    public List<ActivitySummary> getCustomSummary() {
        Instant startDate = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.DAYS);
        Instant endDate =  Instant.now().truncatedTo(ChronoUnit.DAYS);

        return enrich(activitySessionRepository.getCustomSummary(startDate, endDate));
    }

    public List<ActivitySummary> getDailySummary() {
        Instant startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();

        return enrich(activitySessionRepository.getDailySummary(startOfDay));
    }

    public List<ActivitySummary> getWeeklySummary() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        Instant startOfWeek = now
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .truncatedTo(ChronoUnit.DAYS)
                .toInstant();

        Instant endOfNow = Instant.now();

        return enrich(activitySessionRepository.getWeeklySummary(startOfWeek, endOfNow));
    }

    public List<ActivitySummary> getMonthlySummary() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        Instant startOfMonth = now
                .with(TemporalAdjusters.firstDayOfMonth())
                .truncatedTo(ChronoUnit.DAYS)
                .toInstant();

        Instant endOfNow = Instant.now();

        return  enrich(activitySessionRepository.getMonthlySummary(startOfMonth, endOfNow));
    }

    public List<ActivitySummary> getLast7DaysSummary() {
        Instant startDay = Instant.now().minus(Duration.ofDays(7));
        Instant endDay = Instant.now();

        return enrich(activitySessionRepository.getLast7DaysSummary(startDay, endDay));
    }

    public List<ActivitySummary> getLast30DaysSummary() {
        Instant startDay = Instant.now().minus(Duration.ofDays(30));
        Instant endDay = Instant.now();

        return  enrich(activitySessionRepository.getLast30DaysSummary(startDay, endDay));
    }

    private List<ActivitySummary> enrich(List<Object[]> rawData) {
        List<ActivityRule> allRules = activityRuleRepository.findAll();

        List<NormalizedRule> processRules = new ArrayList<>();
        List<NormalizedRule> titleRules = new ArrayList<>();
        for(ActivityRule rule : allRules) {
            String currentPattern = rule.getPattern().replaceAll(" ", "").toLowerCase();
            NormalizedRule currentRule = new NormalizedRule(currentPattern, rule.getCategory(), rule.getDomain());
            if(rule.getRuleType() == RuleType.PROCESS) processRules.add(currentRule);
            else if(rule.getRuleType() == RuleType.TITLE) titleRules.add(currentRule);
        }

        List<ActivitySummary> results = new ArrayList<>();

        for(Object[] currentData : rawData) {
            String processName = currentData[0] != null ? currentData[0].toString() : "";
            String cleanProcessName = processName.replaceAll(" ", "").toLowerCase();

            String windowName = currentData[1] != null ? currentData[1].toString() : "";
            String cleanWindowName = windowName.replaceAll(" ", "").toLowerCase();

            Long duration = ((Number) currentData[2]).longValue();

            String label = windowName.isEmpty() ? processName : windowName;
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

            results.add(new ActivitySummary(processName, label, duration,  category, domain));
        }
        return results;
    }
}
