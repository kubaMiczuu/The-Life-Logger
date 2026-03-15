package com.thelifelogger.serverspring.service;

import com.thelifelogger.serverspring.model.ActivityRule;
import com.thelifelogger.serverspring.model.ActivitySession;
import com.thelifelogger.serverspring.model.RuleType;
import com.thelifelogger.serverspring.repository.ActivityRuleRepository;
import com.thelifelogger.serverspring.repository.ActivitySessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {
    @Mock
    private ActivitySessionRepository activitySessionRepository;

    @Mock
    private ActivityRuleRepository activityRuleRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private ActivityService activityService;

    private static final Instant FIXED_NOW =
            LocalDate.of(2026, 3, 15)
                    .atTime(12, 0)
                    .toInstant(ZoneOffset.UTC);

    private ActivitySession sessionWith(Instant start, Instant lastSeen) {
        ActivitySession session = new ActivitySession();
        session.setProcessName("chrome.exe");
        session.setWindowTitle("Google");
        session.setStartTime(start);
        session.setLastSeen(lastSeen);
        return session;
    }
    @Nested
    class ProcessPingTests {
        @BeforeEach
        void setUp() {
            given(clock.instant()).willReturn(FIXED_NOW);
        }

        @Test
        void shouldCreateNewSession() {
            given(activitySessionRepository.findFirstByEndTimeIsNullOrderByStartTimeDesc())
                    .willReturn(Optional.empty());

            activityService.processPing("chrome.exe", "Google");

            verify(activitySessionRepository, times(1)).save(any(ActivitySession.class));
        }

        @Test
        void shouldUpdateLastSeen() {
            Instant start = FIXED_NOW.minusSeconds(30);
            Instant lastSeen = FIXED_NOW.minusSeconds(5);
            ActivitySession session = sessionWith(start, lastSeen);
            given(activitySessionRepository.findFirstByEndTimeIsNullOrderByStartTimeDesc())
                    .willReturn(Optional.of(session));

            activityService.processPing("chrome.exe", "Google");

            assertThat(session.getLastSeen()).isAfter(lastSeen);
            verify(activitySessionRepository, never()).save(any());
            verify(activitySessionRepository, never()).delete(any());
        }

        @Test
        void shouldCloseSessionAndCreateNew() {
            Instant start = FIXED_NOW.minusSeconds(60);
            Instant lastSeen = FIXED_NOW.minusSeconds(10);
            ActivitySession session = sessionWith(start, lastSeen);
            given(activitySessionRepository.findFirstByEndTimeIsNullOrderByStartTimeDesc())
                    .willReturn(Optional.of(session));

            activityService.processPing("idea64.exe", "Coding");

            assertThat(session.getEndTime()).isEqualTo(lastSeen);
            assertThat(session.getDurationSeconds()).isEqualTo(50L);

            verify(activitySessionRepository, times(2)).save(any(ActivitySession.class));
            verify(activitySessionRepository, never()).delete(any());

        }

        @Test
        void shouldDeleteSessionAndCreateNew() {
            Instant start = FIXED_NOW.minusSeconds(5);
            Instant lastSeen = FIXED_NOW.minusSeconds(2);
            ActivitySession session = sessionWith(start, lastSeen);
            given(activitySessionRepository.findFirstByEndTimeIsNullOrderByStartTimeDesc())
                    .willReturn(Optional.of(session));

            activityService.processPing("idea64.exe", "Coding");

            verify(activitySessionRepository, times(1)).delete(session);
            verify(activitySessionRepository, times(1)).save(any(ActivitySession.class));

            assertThat(session.getEndTime()).isNull();
        }
    }

    private ActivitySession abandonedSession(Instant start, Instant lastSeen) {
        ActivitySession session = new ActivitySession();
        session.setStartTime(start);
        session.setLastSeen(lastSeen);
        return session;
    }
    @Nested
    class CloseAbandonedSessionsTests {
        @BeforeEach
        void setUp() {
            given(clock.instant()).willReturn(FIXED_NOW);
        }

        @Test
        void shouldDoNothingWhenNoAbandonedSession() {
            given(activitySessionRepository.findAllByEndTimeIsNullAndLastSeenBefore(any()))
                    .willReturn(List.of());

            activityService.closeAbandonedSession();

            verify(activitySessionRepository, never()).save(any());
            verify(activitySessionRepository, never()).delete(any());
        }

        @Test
        void shouldCloseAllAbandonedSessions() {
            Instant start1 = FIXED_NOW.minusSeconds(200);
            Instant lastSeen1 = FIXED_NOW.minusSeconds(100);

            Instant start2 = FIXED_NOW.minusSeconds(50);
            Instant lastSeen2 = FIXED_NOW.minusSeconds(10);

            ActivitySession session1 = abandonedSession(start1, lastSeen1);
            ActivitySession session2 = abandonedSession(start2, lastSeen2);

            given(activitySessionRepository.findAllByEndTimeIsNullAndLastSeenBefore(any()))
                    .willReturn(List.of(session1, session2));

            activityService.closeAbandonedSession();

            assertThat(session1.getEndTime()).isEqualTo(lastSeen1);
            assertThat(session1.getDurationSeconds()).isEqualTo(100L);

            assertThat(session2.getEndTime()).isEqualTo(lastSeen2);
            assertThat(session2.getDurationSeconds()).isEqualTo(40L);
        }
    }

    private ActivityRule ruleWith(String domain) {
        ActivityRule rule = new ActivityRule();
        rule.setPattern("pattern");
        rule.setCategory("category");
        rule.setDomain(domain);
        return rule;
    }
    @Nested
    class ProcessRuleTests {
        @Test
        void shouldSetRuleTypeToTitle() {
            ActivityRule rule = ruleWith("google.com");

            activityService.processRule(rule);

            ArgumentCaptor<ActivityRule> captor = ArgumentCaptor.forClass(ActivityRule.class);
            verify(activityRuleRepository).save(captor.capture());
            assertThat(captor.getValue().getRuleType()).isEqualTo(RuleType.TITLE);
        }

        @Test
        void shouldSetRuleTypeToProcess() {
            ActivityRule rule = ruleWith("");

            activityService.processRule(rule);

            ArgumentCaptor<ActivityRule> captor = ArgumentCaptor.forClass(ActivityRule.class);
            verify(activityRuleRepository).save(captor.capture());
            assertThat(captor.getValue().getRuleType()).isEqualTo(RuleType.PROCESS);
        }
    }

    @Nested
    class GetSummaryForRangeTests {
        @Captor
        ArgumentCaptor<Instant> startCaptor = ArgumentCaptor.forClass(Instant.class);

        @Captor
        ArgumentCaptor<Instant> endCaptor = ArgumentCaptor.forClass(Instant.class);

        @BeforeEach
        void setUp() {
            given(clock.instant()).willReturn(FIXED_NOW);
            given(clock.getZone()).willReturn(ZoneOffset.UTC);
            given(activitySessionRepository.getSummaryForRange(any(), any()))
                    .willReturn(List.of());
        }

        @Test
        void shouldHandleDailyNoStartDate() {
            activityService.getSummaryForRange("daily", null, null);

            verify(activitySessionRepository).getSummaryForRange(startCaptor.capture(), endCaptor.capture());

            Instant expectedStart = LocalDate.of(2026, 3, 15)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant();

            assertThat(startCaptor.getValue()).isEqualTo(expectedStart);
            assertThat(endCaptor.getValue()).isEqualTo(FIXED_NOW);
        }

        @Test
        void shouldHandleDailyWithStartDate() {
            LocalDate inputDate = LocalDate.of(2026, 3, 10);

            activityService.getSummaryForRange("daily", inputDate, null);

            verify(activitySessionRepository).getSummaryForRange(startCaptor.capture(), endCaptor.capture());

            assertThat(startCaptor.getValue()).isEqualTo(
                    inputDate.atStartOfDay(ZoneOffset.UTC)
                            .toInstant()
            );
            assertThat(endCaptor.getValue()).isEqualTo(
                    inputDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()
            );
        }

        @Test
        void shouldHandleWeeklyNoStartDate() {
            activityService.getSummaryForRange("weekly", null, null);

            verify(activitySessionRepository).getSummaryForRange(startCaptor.capture(), endCaptor.capture());

            Instant expectedStart = LocalDate.of(2026, 3, 9)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant();

            assertThat(startCaptor.getValue()).isEqualTo(expectedStart);
            assertThat(endCaptor.getValue()).isEqualTo(FIXED_NOW);
        }

        @Test
        void shouldHandleWeeklyWithStartDate() {
            LocalDate inputDate = LocalDate.of(2026, 3, 10);

            activityService.getSummaryForRange("weekly", inputDate, null);

            verify(activitySessionRepository).getSummaryForRange(startCaptor.capture(), endCaptor.capture());

            assertThat(startCaptor.getValue()).isEqualTo(
                    inputDate.atStartOfDay(ZoneOffset.UTC)
                            .toInstant()
            );
            assertThat(endCaptor.getValue()).isEqualTo(
                    inputDate.plusWeeks(1).atStartOfDay(ZoneOffset.UTC).toInstant()
            );
        }

        @Test
        void shouldHandleMonthlyNoStartDate() {
            activityService.getSummaryForRange("monthly", null, null);

            verify(activitySessionRepository).getSummaryForRange(startCaptor.capture(), endCaptor.capture());

            Instant expectedStart = LocalDate.of(2026, 3, 1)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant();

            assertThat(startCaptor.getValue()).isEqualTo(expectedStart);
            assertThat(endCaptor.getValue()).isEqualTo(FIXED_NOW);
        }

        @Test
        void shouldHandleMonthlyWithStartDate() {
            LocalDate inputDate = LocalDate.of(2026, 3, 10);

            activityService.getSummaryForRange("monthly", inputDate, null);

            verify(activitySessionRepository).getSummaryForRange(startCaptor.capture(), endCaptor.capture());

            assertThat(startCaptor.getValue()).isEqualTo(
                    inputDate.atStartOfDay(ZoneOffset.UTC).toInstant()
            );
            assertThat(endCaptor.getValue()).isEqualTo(
                    inputDate.plusMonths(1).atStartOfDay(ZoneOffset.UTC).toInstant()
            );
        }

        @Test
        void shouldHandleYearlyNoStartDate() {
            activityService.getSummaryForRange("yearly", null, null);

            verify(activitySessionRepository).getSummaryForRange(startCaptor.capture(), endCaptor.capture());

            Instant expectedStart = LocalDate.of(2026, 1, 1)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant();

            assertThat(startCaptor.getValue()).isEqualTo(expectedStart);
            assertThat(endCaptor.getValue()).isEqualTo(FIXED_NOW);
        }

        @Test
        void shouldHandleYearlyWithStartDate() {
            LocalDate inputDate = LocalDate.of(2026, 3, 10);

            activityService.getSummaryForRange("yearly", inputDate, null);

            verify(activitySessionRepository).getSummaryForRange(startCaptor.capture(), endCaptor.capture());

            assertThat(startCaptor.getValue()).isEqualTo(
                    inputDate.atStartOfDay(ZoneOffset.UTC).toInstant()
            );
            assertThat(endCaptor.getValue()).isEqualTo(
                    inputDate.plusYears(1).atStartOfDay(ZoneOffset.UTC).toInstant()
            );
        }

        @Test
        void shouldHandleNoRangeNoDates() {
            activityService.getSummaryForRange("", null, null);

            verify(activitySessionRepository).getSummaryForRange(startCaptor.capture(), endCaptor.capture());

            assertThat(startCaptor.getValue()).isEqualTo(Instant.EPOCH);
            assertThat(endCaptor.getValue()).isEqualTo(FIXED_NOW);
        }

        @Test
        void shouldHandleNoRangeWithDates() {
            LocalDate inputDateStart = LocalDate.of(2026, 3, 10);
            LocalDate inputDateEnd = LocalDate.of(2026, 3, 15);

            activityService.getSummaryForRange("", inputDateStart, inputDateEnd);

            verify(activitySessionRepository).getSummaryForRange(startCaptor.capture(), endCaptor.capture());

            assertThat(startCaptor.getValue()).isEqualTo(inputDateStart.atStartOfDay(ZoneOffset.UTC).toInstant());
            assertThat(endCaptor.getValue()).isEqualTo(inputDateEnd.atStartOfDay(ZoneOffset.UTC).toInstant());
        }
    }

}
