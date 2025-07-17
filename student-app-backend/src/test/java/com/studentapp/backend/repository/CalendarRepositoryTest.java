package com.studentapp.backend.repository;

import com.studentapp.common.model.CalendarEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CalendarRepository, verifying custom method mocks.
 */
class CalendarRepositoryTest {

    @Mock
    private CalendarRepository calendarRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifies findAllByStartTimeBetween can be called on a mock.
     */
    @Test
    void findAllByStartTimeBetweenCanBeCalled() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        when(calendarRepository.findAllByStartTimeBetween(start, end)).thenReturn(List.of());
        List<CalendarEvent> result = calendarRepository.findAllByStartTimeBetween(start, end);
        assertThat(result).isNotNull();
    }

    /**
     * Verifies findAllByUserId can be called on a mock.
     */
    @Test
    void findAllByUserIdCanBeCalled() {
        UUID userId = UUID.randomUUID();
        when(calendarRepository.findAllByUserId(userId)).thenReturn(List.of());
        List<CalendarEvent> result = calendarRepository.findAllByUserId(userId);
        assertThat(result).isNotNull();
    }

    /**
     * Verifies findAllByUserIdAndStartTimeBetween can be called on a mock.
     */
    @Test
    void findAllByUserIdAndStartTimeBetweenCanBeCalled() {
        UUID userId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        when(calendarRepository.findAllByUserIdAndStartTimeBetween(userId, start, end)).thenReturn(List.of());
        List<CalendarEvent> result = calendarRepository.findAllByUserIdAndStartTimeBetween(userId, start, end);
        assertThat(result).isNotNull();
    }
} 