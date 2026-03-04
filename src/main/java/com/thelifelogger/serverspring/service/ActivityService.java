package com.thelifelogger.serverspring.service;

import com.thelifelogger.serverspring.model.ActivitySession;
import com.thelifelogger.serverspring.repository.ActivitySessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

                currentSession.setEndTime(currentSession.getLastSeen());
                activitySessionRepository.save(currentSession);

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
}
