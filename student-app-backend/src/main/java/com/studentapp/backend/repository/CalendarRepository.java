package com.studentapp.backend.repository;

import com.studentapp.common.model.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CalendarRepository extends JpaRepository<CalendarEvent, String> {
  List<CalendarEvent> findAllByStartTimeBetween(LocalDateTime start, LocalDateTime end);
  List<CalendarEvent> findAllByUserId(UUID userId);
  List<CalendarEvent> findAllByUserIdAndStartTimeBetween(UUID userId, LocalDateTime start, LocalDateTime end);
}