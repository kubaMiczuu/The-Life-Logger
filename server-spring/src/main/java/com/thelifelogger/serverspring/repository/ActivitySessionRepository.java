package com.thelifelogger.serverspring.repository;

import com.thelifelogger.serverspring.dto.ActivitySummary;
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

    @Query("SELECT new com.thelifelogger.serverspring.dto.ActivitySummary(a.processName, SUM(a.durationSeconds))" +
            "FROM ActivitySession a " +
            "WHERE a.endTime IS NOT NULL " +
            "GROUP BY a.processName " +
            "ORDER BY SUM(a.durationSeconds) DESC")
    List<ActivitySummary> getSummary();

    @Query("SELECT new com.thelifelogger.serverspring.dto.ActivitySummary(a.processName, SUM(a.durationSeconds))" +
            "FROM ActivitySession a " +
            "WHERE a.endTime IS NOT NULL " +
            "AND a.startTime between :startDate AND :endDate " +
            "GROUP BY a.processName " +
            "ORDER BY SUM(a.durationSeconds) DESC")
    List<ActivitySummary> getCustomSummary(Instant startDate, Instant endDate);

    @Query("SELECT new com.thelifelogger.serverspring.dto.ActivitySummary(a.processName, SUM(a.durationSeconds))" +
            "FROM ActivitySession a " +
            "WHERE a.endTime IS NOT NULL " +
            "AND a.startTime >= :day " +
            "GROUP BY a.processName " +
            "ORDER BY SUM(a.durationSeconds) DESC")
    List<ActivitySummary> getDailySummary(Instant day);

    @Query("SELECT new com.thelifelogger.serverspring.dto.ActivitySummary(a.processName, SUM(a.durationSeconds))" +
            "FROM ActivitySession a " +
            "WHERE a.endTime IS NOT NULL " +
            "AND a.startTime between :startOfWeek AND :endOfNow " +
            "GROUP BY a.processName " +
            "ORDER BY SUM(a.durationSeconds) DESC")
    List<ActivitySummary> getWeeklySummary(Instant startOfWeek, Instant endOfNow);

    @Query("SELECT new com.thelifelogger.serverspring.dto.ActivitySummary(a.processName, SUM(a.durationSeconds))" +
            "FROM ActivitySession a " +
            "WHERE a.endTime IS NOT NULL " +
            "AND a.startTime between :startOfMonth AND :endOfNow " +
            "GROUP BY a.processName " +
            "ORDER BY SUM(a.durationSeconds) DESC")
    List<ActivitySummary> getMonthlySummary(Instant startOfMonth, Instant endOfNow);

    @Query("SELECT new com.thelifelogger.serverspring.dto.ActivitySummary(a.processName, SUM(a.durationSeconds))" +
            "FROM ActivitySession a " +
            "WHERE a.endTime IS NOT NULL " +
            "AND a.startTime between :startDay AND :endDay " +
            "GROUP BY a.processName " +
            "ORDER BY SUM(a.durationSeconds) DESC")
    List<ActivitySummary> getLast7DaysSummary(Instant startDay, Instant endDay);

    @Query("SELECT new com.thelifelogger.serverspring.dto.ActivitySummary(a.processName, SUM(a.durationSeconds))" +
            "FROM ActivitySession a " +
            "WHERE a.endTime IS NOT NULL " +
            "AND a.startTime between :startDay AND :endDay " +
            "GROUP BY a.processName " +
            "ORDER BY SUM(a.durationSeconds) DESC")
    List<ActivitySummary> getLast30DaysSummary(Instant startDay, Instant endDay);
}
