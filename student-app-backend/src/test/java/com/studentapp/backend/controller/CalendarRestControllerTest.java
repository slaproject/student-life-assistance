package com.studentapp.backend.controller;

import com.studentapp.backend.security.JwtUtil;
import com.studentapp.backend.service.CalendarService;
import com.studentapp.common.model.CalendarEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CalendarRestController, covering event endpoint scenarios.
 */
class CalendarRestControllerTest {

    @Mock
    private CalendarService calendarService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CalendarRestController controller;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifies getAllEvents returns events for a valid token.
     */
    @Test
    void getAllEventsValidTokenReturnsEvents() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtUtil.extractUserIdFromToken("token")).thenReturn(userId);
        List<CalendarEvent> events = List.of(new CalendarEvent());
        when(calendarService.getAllEvents(userId)).thenReturn(events);
        List<CalendarEvent> result = controller.getAllEvents(request);
        assertThat(result).isEqualTo(events);
    }

    /**
     * Ensures getAllEvents throws for missing Authorization header.
     */
    @Test
    void getAllEventsMissingAuthHeaderThrows() {
        when(request.getHeader("Authorization")).thenReturn(null);
        assertThatThrownBy(() -> controller.getAllEvents(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid or missing Authorization header");
    }

    /**
     * Verifies getEventById returns event for valid token.
     */
    @Test
    void getEventByIdValidTokenReturnsEvent() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtUtil.extractUserIdFromToken("token")).thenReturn(userId);
        Optional<CalendarEvent> event = Optional.of(new CalendarEvent());
        when(calendarService.getEventById("1", userId)).thenReturn(event);
        Optional<CalendarEvent> result = controller.getEventById("1", request);
        assertThat(result).isEqualTo(event);
    }

    /**
     * Verifies getEventsForDate returns events for valid token and date.
     */
    @Test
    void getEventsForDateValidTokenReturnsEvents() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtUtil.extractUserIdFromToken("token")).thenReturn(userId);
        List<CalendarEvent> events = List.of(new CalendarEvent());
        when(calendarService.getEventsForDate(LocalDate.parse("2024-01-01"), userId)).thenReturn(events);
        List<CalendarEvent> result = controller.getEventsForDate("2024-01-01", request);
        assertThat(result).isEqualTo(events);
    }

    /**
     * Verifies createOrUpdateEvent saves event for valid token.
     */
    @Test
    void createOrUpdateEventValidTokenSavesEvent() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtUtil.extractUserIdFromToken("token")).thenReturn(userId);
        CalendarEvent event = new CalendarEvent();
        when(calendarService.saveEvent(event, userId)).thenReturn(event);
        CalendarEvent result = controller.createOrUpdateEvent(event, request);
        assertThat(result).isEqualTo(event);
    }

    /**
     * Verifies deleteEvent calls service for valid token.
     */
    @Test
    void deleteEventValidTokenDeletesEvent() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtUtil.extractUserIdFromToken("token")).thenReturn(userId);
        doNothing().when(calendarService).deleteEvent("1", userId);
        controller.deleteEvent("1", request);
        verify(calendarService).deleteEvent("1", userId);
    }

    /**
     * Verifies updateEvent updates event for valid token.
     */
    @Test
    void updateEventValidTokenUpdatesEvent() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtUtil.extractUserIdFromToken("token")).thenReturn(userId);
        CalendarEvent event = new CalendarEvent();
        when(calendarService.saveEvent(event, userId)).thenReturn(event);
        CalendarEvent result = controller.updateEvent("1", event, request);
        assertThat(result).isEqualTo(event);
        assertThat(event.getId()).isEqualTo("1");
    }

    /**
     * Ensures all endpoints throw for invalid Authorization header.
     */
    @Test
    void allEndpointsInvalidAuthHeaderThrows() {
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");
        assertThatThrownBy(() -> controller.getAllEvents(request)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> controller.getEventById("1", request)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> controller.getEventsForDate("2024-01-01", request)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> controller.createOrUpdateEvent(new CalendarEvent(), request)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> controller.deleteEvent("1", request)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> controller.updateEvent("1", new CalendarEvent(), request)).isInstanceOf(RuntimeException.class);
    }

    /**
     * Ensures all endpoints throw when request is null.
     */
    @Test
    void allEndpointsWithNullRequestThrows() {
        assertThatThrownBy(() -> controller.getAllEvents(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> controller.getEventById("1", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> controller.getEventsForDate("2024-01-01", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> controller.createOrUpdateEvent(new CalendarEvent(), null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> controller.deleteEvent("1", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> controller.updateEvent("1", new CalendarEvent(), null)).isInstanceOf(NullPointerException.class);
    }

    /**
     * Ensures endpoints with null or empty Authorization header throw.
     */
    @Test
    void allEndpointsWithNullOrEmptyAuthHeaderThrows() {
        when(request.getHeader("Authorization")).thenReturn("");
        assertThatThrownBy(() -> controller.getAllEvents(request)).isInstanceOf(RuntimeException.class);
        when(request.getHeader("Authorization")).thenReturn(null);
        assertThatThrownBy(() -> controller.getAllEvents(request)).isInstanceOf(RuntimeException.class);
    }

    /**
     * Verifies getEventsForDate throws for null date.
     */
    @Test
    void getEventsForDateWithNullDateThrows() {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtUtil.extractUserIdFromToken("token")).thenReturn(userId);
        assertThatThrownBy(() -> controller.getEventsForDate(null, request)).isInstanceOf(NullPointerException.class);
    }

    /**
     * Verifies updateEvent throws for null event.
     */
    @Test
    void updateEventWithNullEventThrows() {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtUtil.extractUserIdFromToken("token")).thenReturn(userId);
        assertThatThrownBy(() -> controller.updateEvent("1", null, request)).isInstanceOf(NullPointerException.class);
    }
} 