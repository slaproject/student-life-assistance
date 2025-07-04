package com.studentapp.frontend.client;

import com.studentapp.common.model.CalendarEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * This class handles HTTP communication with the backend API.
 * In a real application, this would use HttpClient, Retrofit, or WebClient.
 */
public class CalendarApiClient {

  private final String backendUrl = "http://localhost:8080/api/calendar/events";
  private final ObjectMapper objectMapper = new ObjectMapper();
  private String jwtToken;

  public CalendarApiClient() {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public List<CalendarEvent> getEvents() {
    try {
      URL url = new URL(backendUrl);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");
      if (jwtToken != null) {
        conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
      }
      if (conn.getResponseCode() != 200) {
        System.out.println("Failed to get events. Response code: " + conn.getResponseCode());
        return new ArrayList<>();
      }
      InputStream is = conn.getInputStream();
      List<CalendarEvent> events = objectMapper.readValue(is, new TypeReference<List<CalendarEvent>>(){});
      is.close();
      conn.disconnect();
      System.out.println("Retrieved " + events.size() + " events from API");
      return events;
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  public CalendarEvent addEvent(CalendarEvent event) {
    try {
      URL url = new URL(backendUrl);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      System.out.println("CalendarApiClient " + jwtToken);
      if (jwtToken != null) {
        conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
      } else {
        System.out.println("No JWT token found");
      }
      conn.setDoOutput(true);
      OutputStream os = conn.getOutputStream();
      objectMapper.writeValue(os, event);
      os.flush();
      os.close();
      System.out.println(conn.getResponseCode());
      InputStream errorStream = conn.getErrorStream();
      if (errorStream != null) {
        String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Error Response: " + errorResponse);
      }
      if (conn.getResponseCode() != 200) {
        return null;
      }
      InputStream is = conn.getInputStream();
      CalendarEvent created = objectMapper.readValue(is, CalendarEvent.class);
      is.close();
      conn.disconnect();
      System.out.println("Hello   " + created.getEventName()+ " " + created.getEventType());
      return created;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public CalendarEvent updateEvent(CalendarEvent event) {
    // For simplicity, use POST (same as addEvent)
    return addEvent(event);
  }

  public void setJwtToken(String token) {
    this.jwtToken = token;
  }

  public boolean deleteEvent(String id) {
    try {
      URL url = new URL(backendUrl + "/" + id);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("DELETE");
      if (jwtToken != null) {
        conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
      }
      int responseCode = conn.getResponseCode();
      conn.disconnect();
      return responseCode == 200 || responseCode == 204;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public static String signup(String username, String email, String password) {
    try {
      URL url = new URL("http://localhost:8080/api/auth/signup");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setDoOutput(true);
      String jsonInput = String.format("{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}", username, email, password);
      try (OutputStream os = conn.getOutputStream()) {
        byte[] input = jsonInput.getBytes("utf-8");
        os.write(input, 0, input.length);
      }
      InputStream is = (conn.getResponseCode() < 400) ? conn.getInputStream() : conn.getErrorStream();
      String responseBody = null;
      if (is != null) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        responseBody = s.hasNext() ? s.next() : "";
        is.close();
      }
      conn.disconnect();
      return responseBody != null && !responseBody.isEmpty() ? responseBody : "Signup failed: " + conn.getResponseCode();
    } catch (Exception e) {
      return "Signup error: " + e.getMessage();
    }
  }

  public static String login(String username, String password) {
    try {
      URL url = new URL("http://localhost:8080/api/auth/login");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setDoOutput(true);
      String jsonInput = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
      try (OutputStream os = conn.getOutputStream()) {
        byte[] input = jsonInput.getBytes("utf-8");
        os.write(input, 0, input.length);
      }
      int code = conn.getResponseCode();
      if (code == 200) {
        java.util.Scanner s = new java.util.Scanner(conn.getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
      } else {
        return "Invalid username or password";
      }
    } catch (Exception e) {
      return "Login error: " + e.getMessage();
    }
  }

  public static String getEvents(String jwtToken) {
    try {
      URL url = new URL("http://localhost:8080/api/calendar/events");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
      int code = conn.getResponseCode();
      if (code == 200) {
        InputStream is = conn.getInputStream();
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
      } else {
        return "Error: " + code;
      }
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  public static String addEvent(String eventJson, String jwtToken) {
    try {
      URL url = new URL("http://localhost:8080/api/calendar/events");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
      conn.setDoOutput(true);
      try (OutputStream os = conn.getOutputStream()) {
        byte[] input = eventJson.getBytes("utf-8");
        os.write(input, 0, input.length);
      }
      int code = conn.getResponseCode();
      if (code == 200) {
        InputStream is = conn.getInputStream();
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
      } else {
        return "Error: " + code;
      }
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  public static String updateEvent(String eventId, String eventJson, String jwtToken) {
    try {
      URL url = new URL("http://localhost:8080/api/calendar/events/" + eventId);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("PUT");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
      conn.setDoOutput(true);
      try (OutputStream os = conn.getOutputStream()) {
        byte[] input = eventJson.getBytes("utf-8");
        os.write(input, 0, input.length);
      }
      int code = conn.getResponseCode();
      if (code == 200) {
        InputStream is = conn.getInputStream();
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
      } else {
        return "Error: " + code;
      }
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  public static String deleteEvent(String eventId, String jwtToken) {
    try {
      URL url = new URL("http://localhost:8080/api/calendar/events/" + eventId);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("DELETE");
      conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
      int code = conn.getResponseCode();
      if (code == 200 || code == 204) {
        return "Event deleted successfully";
      } else {
        return "Error: " + code;
      }
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }
}