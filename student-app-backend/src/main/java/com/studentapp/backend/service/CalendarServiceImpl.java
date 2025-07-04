package com.studentapp.backend.service;

import com.studentapp.common.model.CalendarEvent;
import com.studentapp.backend.repository.CalendarRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CalendarServiceImpl implements CalendarService {

  private final CalendarRepository calendarRepository;

  public CalendarServiceImpl(CalendarRepository calendarRepository) {
    this.calendarRepository = calendarRepository;
  }

  @Override
  public CalendarEvent saveEvent(CalendarEvent event, UUID userId) {
    event.setUserId(userId);
    return calendarRepository.save(event);
  }

  @Override
  public Optional<CalendarEvent> getEventById(String id, UUID userId) {
    Optional<CalendarEvent> event = calendarRepository.findById(id);
    return event.filter(e -> userId.equals(e.getUserId()));
  }

  @Override
  public List<CalendarEvent> getAllEvents(UUID userId) {
    return calendarRepository.findAllByUserId(userId);
  }

  @Override
  public List<CalendarEvent> getEventsForDate(LocalDate date, UUID userId) {
    LocalDateTime start = date.atStartOfDay();
    LocalDateTime end = date.atTime(23, 59, 59);
    return calendarRepository.findAllByUserIdAndStartTimeBetween(userId, start, end);
  }

  @Override
  public void deleteEvent(String id, UUID userId) {
    Optional<CalendarEvent> event = calendarRepository.findById(id);
    event.filter(e -> userId.equals(e.getUserId())).ifPresent(e -> calendarRepository.deleteById(id));
  }
}