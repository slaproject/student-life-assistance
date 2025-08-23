package com.studentapp.frontend.client;

import com.studentapp.common.model.CalendarEvent;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;

/**
 * This class handles HTTP communication with the backend API.
 * In a real application, this would use HttpClient, Retrofit, or WebClient.
 */
public class CalendarApiClient {

  private static final String backendUrl = "http://localhost:8080/api/calendar/events";
  private final ObjectMapper objectMapper = new ObjectMapper();
  private String jwtToken;
  private final HttpClient httpClient;

  public CalendarApiClient() {
    this(HttpClient.newHttpClient());
  }

  public CalendarApiClient(HttpClient httpClient) {
    this.httpClient = httpClient;
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public List<CalendarEvent> getEvents() {
    try {
      HttpRequest.Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(backendUrl))
        .timeout(Duration.ofSeconds(10))
        .header("Accept", "application/json")
        .GET();
      if (jwtToken != null) {
        builder.header("Authorization", "Bearer " + jwtToken);
      }
      HttpRequest request = builder.build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) {
        return new ArrayList<>();
      }
      List<CalendarEvent> events = objectMapper.readValue(response.body(), new TypeReference<List<CalendarEvent>>(){});
      return events;
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  public CalendarEvent addEvent(CalendarEvent event) {
    try {
      String json = objectMapper.writeValueAsString(event);
      HttpRequest.Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(backendUrl))
        .timeout(Duration.ofSeconds(10))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(json));
      if (jwtToken != null) {
        builder.header("Authorization", "Bearer " + jwtToken);
      }
      HttpRequest request = builder.build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) {
        return null;
      }
      CalendarEvent created = objectMapper.readValue(response.body(), CalendarEvent.class);
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

  public void clearJwtToken() {
    this.jwtToken = null;
  }

  public boolean deleteEvent(String id) {
    try {
      HttpRequest.Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(backendUrl + "/" + id))
        .timeout(Duration.ofSeconds(10))
        .DELETE();
      if (jwtToken != null) {
        builder.header("Authorization", "Bearer " + jwtToken);
      }
      HttpRequest request = builder.build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      return response.statusCode() == 200 || response.statusCode() == 204;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public static String signup(String username, String email, String password) {
    try {
      HttpClient client = HttpClient.newHttpClient();
      String jsonInput = String.format("{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}", username, email, password);
      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8080/api/auth/signup"))
        .timeout(Duration.ofSeconds(10))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
        .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() < 400) {
        return response.body();
      } else {
        return "Signup failed: " + response.statusCode();
      }
    } catch (Exception e) {
      return "Signup error: " + e.getMessage();
    }
  }

  public static String login(String username, String password) {
    try {
      HttpClient client = HttpClient.newHttpClient();
      String jsonInput = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8080/api/auth/login"))
        .timeout(Duration.ofSeconds(10))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
        .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        return response.body();
      } else {
        return "Invalid username or password";
      }
    } catch (Exception e) {
      return "Login error: " + e.getMessage();
    }
  }

  public static String getEvents(String jwtToken) {
    try {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8080/api/calendar/events"))
        .timeout(Duration.ofSeconds(10))
        .header("Authorization", "Bearer " + jwtToken)
        .GET()
        .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        return response.body();
      } else {
        return "Error: " + response.statusCode();
      }
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  public static String addEvent(String eventJson, String jwtToken) {
    try {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8080/api/calendar/events"))
        .timeout(Duration.ofSeconds(10))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + jwtToken)
        .POST(HttpRequest.BodyPublishers.ofString(eventJson))
        .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        return response.body();
      } else {
        return "Error: " + response.statusCode();
      }
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  public static String updateEvent(String eventId, String eventJson, String jwtToken) {
    try {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8080/api/calendar/events/" + eventId))
        .timeout(Duration.ofSeconds(10))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + jwtToken)
        .PUT(HttpRequest.BodyPublishers.ofString(eventJson))
        .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        return response.body();
      } else {
        return "Error: " + response.statusCode();
      }
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  public static String deleteEvent(String eventId, String jwtToken) {
    try {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8080/api/calendar/events/" + eventId))
        .timeout(Duration.ofSeconds(10))
        .header("Authorization", "Bearer " + jwtToken)
        .DELETE()
        .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200 || response.statusCode() == 204) {
        return "Event deleted successfully";
      } else {
        return "Error: " + response.statusCode();
      }
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }
}