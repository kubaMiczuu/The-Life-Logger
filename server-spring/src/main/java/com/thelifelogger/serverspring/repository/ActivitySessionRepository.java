package com.thelifelogger.serverspring.repository;

import com.thelifelogger.serverspring.model.ActivitySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivitySessionRepository extends JpaRepository<ActivitySession, Long> {

    Optional<ActivitySession> findFirstByEndTimeIsNullOrderByStartTimeDesc();

    List<ActivitySession> findAllByEndTimeIsNullAndLastSeenBefore(Instant threshold);
}
