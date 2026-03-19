package com.thelifelogger.serverspring.repository;

import com.thelifelogger.serverspring.model.ActivitySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivitySessionRepository extends JpaRepository<ActivitySession, Long> {

    Optional<ActivitySession> findFirstByEndTimeIsNullOrderByStartTimeDesc();

    List<ActivitySession> findAllByEndTimeIsNullAndLastSeenBefore(Instant threshold);

    @Query("SELECT a.processName, a.windowTitle, SUM(a.durationSeconds)" +
            "FROM ActivitySession a " +
            "WHERE a.endTime IS NOT NULL " +
            "AND a.startTime between :startDate AND :endDate " +
            "GROUP BY a.processName, a.windowTitle " +
            "ORDER BY SUM(a.durationSeconds) DESC")
    List<Object[]> getSummaryForRange(Instant startDate, Instant endDate);

    @Query("SELECT a.startTime, a.endTime " +
            "FROM ActivitySession a " +
            "WHERE a.startTime BETWEEN :startDate AND :endDate " +
            "AND a.endTime IS NOT NULL " +
            "ORDER BY a.startTime ASC")
    List<Object[]> getTimeForRange(Instant startDate, Instant endDate);
}
