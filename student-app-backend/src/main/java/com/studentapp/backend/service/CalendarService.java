package com.studentapp.backend.service;

import com.studentapp.common.model.CalendarEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CalendarService {
  CalendarEvent saveEvent(CalendarEvent event, UUID userId);
  Optional<CalendarEvent> getEventById(String id, UUID userId);
  List<CalendarEvent> getAllEvents(UUID userId);
  List<CalendarEvent> getEventsForDate(LocalDate date, UUID userId);
  void deleteEvent(String id, UUID userId);
}