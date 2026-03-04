package com.thelifelogger.serverspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name="activity_sessions")
public class ActivitySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="process_name", nullable=false)
    private String processName;

    @Column(name="window_title")
    private String windowTitle;

    @Column(name="start_time", nullable=false)
    private Instant startTime;

    @Column(name="last_seen", nullable=false)
    private Instant lastSeen;

    @Column(name="end_time")
    private Instant endTime;

    @Column(name="duration_seconds")
    private Long durationSeconds;
}
