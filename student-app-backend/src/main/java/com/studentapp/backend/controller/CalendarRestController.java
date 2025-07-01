package com.studentapp.backend.controller;

import com.studentapp.common.model.CalendarEvent;
import com.studentapp.backend.service.CalendarService;
import com.studentapp.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/calendar")
public class CalendarRestController {

  private final CalendarService calendarService;
  @Autowired
  private JwtUtil jwtUtil;

  public CalendarRestController(CalendarService calendarService) {
    this.calendarService = calendarService;
  }

  private UUID extractUserIdFromRequest(HttpServletRequest request) throws NoSuchFieldException {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      String userIdStr = null;
      try {
        userIdStr = (String) io.jsonwebtoken.Jwts.parser()
          .setSigningKey((String) jwtUtil.getClass().getDeclaredField("jwtSecret").get(jwtUtil))
          .parseClaimsJws(token)
          .getBody()
          .get("userId");
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
      return UUID.fromString(userIdStr);
    }
    throw new RuntimeException("Invalid or missing Authorization header");
  }

  @GetMapping("/events")
  public List<CalendarEvent> getAllEvents(HttpServletRequest request) throws NoSuchFieldException {
    UUID userId = extractUserIdFromRequest(request);
    return calendarService.getAllEvents(userId);
  }

  @GetMapping("/events/{id}")
  public Optional<CalendarEvent> getEventById(@PathVariable("id") String id, HttpServletRequest request) throws NoSuchFieldException {
    UUID userId = extractUserIdFromRequest(request);
    return calendarService.getEventById(id, userId);
  }

  @GetMapping("/events/date/{date}")
  public List<CalendarEvent> getEventsForDate(@PathVariable("date") String date, HttpServletRequest request) throws NoSuchFieldException {
    UUID userId = extractUserIdFromRequest(request);
    return calendarService.getEventsForDate(LocalDate.parse(date), userId);
  }

  @PostMapping("/events")
  public CalendarEvent createOrUpdateEvent(@RequestBody CalendarEvent event, HttpServletRequest request) throws NoSuchFieldException {
    UUID userId = extractUserIdFromRequest(request);
    return calendarService.saveEvent(event, userId);
  }

  @DeleteMapping("/events/{id}")
  public void deleteEvent(@PathVariable("id") String id, HttpServletRequest request) throws NoSuchFieldException {
    UUID userId = extractUserIdFromRequest(request);
    calendarService.deleteEvent(id, userId);
  }

  @PutMapping("/events/{id}")
  public CalendarEvent updateEvent(@PathVariable("id") String id, @RequestBody CalendarEvent event, HttpServletRequest request) throws NoSuchFieldException {
    UUID userId = extractUserIdFromRequest(request);
    event.setId(id);
    return calendarService.saveEvent(event, userId);
  }
}