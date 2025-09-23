package com.studentapp.common.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "calendar_events")
public class CalendarEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String id;

  @Column(name = "event_name", nullable = false)
  private String eventName;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "start_time", nullable = false)
  private LocalDateTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalDateTime endTime;

  @Column(name = "meeting_links", length = 512)
  private String meetingLinks;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type")
  private EventType eventType;

  @Column(nullable = false)
  private UUID userId;

  public enum EventType {
    MEETING, PERSONAL, FINANCIAL, APPOINTMENT, OTHER
  }

  public CalendarEvent() {
    // JPA requires a no-arg constructor
  }

  public CalendarEvent(String title,String description, LocalDateTime startTime,
                       LocalDateTime endTime, String meetingLinks) {
    this.eventName = title;
    this.description = description;
    this.startTime = startTime;
    this.endTime = endTime;
    this.eventType = EventType.OTHER;
    this.meetingLinks = meetingLinks;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEventName() { return eventName; }
  public void setEventName(String eventName) { this.eventName = eventName; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public LocalDateTime getStartTime() { return startTime; }
  public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

  public LocalDateTime getEndTime() { return endTime; }
  public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

  public String getMeetingLinks() { return meetingLinks; }
  public void setMeetingLinks(String meetingLinks) { this.meetingLinks = meetingLinks; }

  public EventType getEventType() { return eventType; }
  public void setEventType(EventType eventType) { this.eventType = eventType; }

  public UUID getUserId() { return userId; }
  public void setUserId(UUID userId) { this.userId = userId; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CalendarEvent)) return false;
    CalendarEvent that = (CalendarEvent) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() { return Objects.hash(id); }
}

