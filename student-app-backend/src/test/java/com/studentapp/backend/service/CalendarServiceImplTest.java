package com.studentapp.backend.service;

import com.studentapp.backend.repository.CalendarRepository;
import com.studentapp.common.model.CalendarEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CalendarServiceImpl, covering event CRUD and validation logic.
 */
class CalendarServiceImplTest {

    @Mock
    private CalendarRepository calendarRepository;

    @InjectMocks
    private CalendarServiceImpl service;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifies that saveEvent sets the userId and saves the event.
     */
    @Test
    void saveEventSetsUserIdAndSaves() {
        CalendarEvent event = new CalendarEvent();
        when(calendarRepository.save(event)).thenReturn(event);
        CalendarEvent result = service.saveEvent(event, userId);
        assertThat(result).isEqualTo(event);
        assertThat(event.getUserId()).isEqualTo(userId);
        verify(calendarRepository).save(event);
    }

    /**
     * Ensures getEventById returns the event if the userId matches.
     */
    @Test
    void getEventByIdUserIdMatchesReturnsEvent() {
        CalendarEvent event = new CalendarEvent();
        event.setUserId(userId);
        when(calendarRepository.findById("1")).thenReturn(Optional.of(event));
        Optional<CalendarEvent> result = service.getEventById("1", userId);
        assertThat(result).contains(event);
    }

    /**
     * Ensures getEventById returns empty if the userId does not match.
     */
    @Test
    void getEventByIdUserIdDoesNotMatchReturnsEmpty() {
        CalendarEvent event = new CalendarEvent();
        event.setUserId(UUID.randomUUID());
        when(calendarRepository.findById("1")).thenReturn(Optional.of(event));
        Optional<CalendarEvent> result = service.getEventById("1", userId);
        assertThat(result).isEmpty();
    }

    /**
     * Ensures getEventById returns empty if the event is not found.
     */
    @Test
    void getEventByIdEventNotFoundReturnsEmpty() {
        when(calendarRepository.findById("1")).thenReturn(Optional.empty());
        Optional<CalendarEvent> result = service.getEventById("1", userId);
        assertThat(result).isEmpty();
    }

    /**
     * Verifies getAllEvents returns all events for the userId.
     */
    @Test
    void getAllEventsReturnsEvents() {
        List<CalendarEvent> events = List.of(new CalendarEvent());
        when(calendarRepository.findAllByUserId(userId)).thenReturn(events);
        List<CalendarEvent> result = service.getAllEvents(userId);
        assertThat(result).isEqualTo(events);
    }

    /**
     * Verifies getEventsForDate returns events within the date range.
     */
    @Test
    void getEventsForDateReturnsEvents() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        List<CalendarEvent> events = List.of(new CalendarEvent());
        when(calendarRepository.findAllByUserIdAndStartTimeBetween(userId, start, end)).thenReturn(events);
        List<CalendarEvent> result = service.getEventsForDate(date, userId);
        assertThat(result).isEqualTo(events);
    }

    /**
     * Ensures deleteEvent deletes the event if the userId matches.
     */
    @Test
    void deleteEventUserIdMatchesDeletes() {
        CalendarEvent event = new CalendarEvent();
        event.setUserId(userId);
        when(calendarRepository.findById("1")).thenReturn(Optional.of(event));
        service.deleteEvent("1", userId);
        verify(calendarRepository).deleteById("1");
    }

    /**
     * Ensures deleteEvent does nothing if the userId does not match.
     */
    @Test
    void deleteEventUserIdDoesNotMatchDoesNothing() {
        CalendarEvent event = new CalendarEvent();
        event.setUserId(UUID.randomUUID());
        when(calendarRepository.findById("1")).thenReturn(Optional.of(event));
        service.deleteEvent("1", userId);
        verify(calendarRepository, never()).deleteById(any());
    }

    /**
     * Ensures deleteEvent does nothing if the event is not found.
     */
    @Test
    void deleteEventEventNotFoundDoesNothing() {
        when(calendarRepository.findById("1")).thenReturn(Optional.empty());
        service.deleteEvent("1", userId);
        verify(calendarRepository, never()).deleteById(any());
    }

    /**
     * Verifies saveEvent throws NullPointerException for null event.
     */
    @Test
    void saveEventWithNullEventThrows() {
        assertThatThrownBy(() -> service.saveEvent(null, userId)).isInstanceOf(NullPointerException.class);
    }

    /**
     * Verifies getEventsForDate throws NullPointerException for null date.
     */
    @Test
    void getEventsForDateWithNullDateThrows() {
        assertThatThrownBy(() -> service.getEventsForDate(null, userId)).isInstanceOf(NullPointerException.class);
    }

} 