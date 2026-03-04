package com.thelifelogger.serverspring.service;

import com.thelifelogger.serverspring.dto.ActivitySummary;
import com.thelifelogger.serverspring.model.ActivitySession;
import com.thelifelogger.serverspring.repository.ActivitySessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ActivityService {
    private final ActivitySessionRepository activitySessionRepository;

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
        return activitySessionRepository.getSummary();
    }

    public List<ActivitySummary> getCustomSummary() {
        Instant startDate = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.DAYS);
        Instant endDate =  Instant.now().truncatedTo(ChronoUnit.DAYS);

        return activitySessionRepository.getCustomSummary(startDate, endDate);
    }

    public List<ActivitySummary> getDailySummary() {
        Instant startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();

        return activitySessionRepository.getDailySummary(startOfDay);
    }

    public List<ActivitySummary> getWeeklySummary() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        Instant startOfWeek = now
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .truncatedTo(ChronoUnit.DAYS)
                .toInstant();

        Instant endOfNow = Instant.now();

        return activitySessionRepository.getWeeklySummary(startOfWeek, endOfNow);
    }

    public List<ActivitySummary> getMonthlySummary() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        Instant startOfMonth = now
                .with(TemporalAdjusters.firstDayOfMonth())
                .truncatedTo(ChronoUnit.DAYS)
                .toInstant();

        Instant endOfNow = Instant.now();

        return  activitySessionRepository.getMonthlySummary(startOfMonth, endOfNow);
    }

    public List<ActivitySummary> getLast7DaysSummary() {
        Instant startDay = Instant.now().minus(Duration.ofDays(7));
        Instant endDay = Instant.now();

        return activitySessionRepository.getLast7DaysSummary(startDay, endDay);
    }

    public List<ActivitySummary> getLast30DaysSummary() {
        Instant startDay = Instant.now().minus(Duration.ofDays(30));
        Instant endDay = Instant.now();

        return  activitySessionRepository.getLast30DaysSummary(startDay, endDay);
    }

}
