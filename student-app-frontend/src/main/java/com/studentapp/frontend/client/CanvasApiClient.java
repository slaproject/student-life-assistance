package com.studentapp.frontend.client;

import com.google.gson.Gson;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class CanvasApiClient {
    
    private final HttpClient httpClient;
    private final Gson gson;
    private String apiToken;
    private String canvasInstanceUrl;
    
    public CanvasApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        loadConfiguration();
    }
    
    private void loadConfiguration() {
        try {
            java.util.Properties props = new java.util.Properties();
            java.io.InputStream input = getClass().getClassLoader().getResourceAsStream("canvas-config.properties");
            if (input != null) {
                props.load(input);
                input.close();
                
                this.apiToken = props.getProperty("canvas.api.token", "");
                this.canvasInstanceUrl = props.getProperty("canvas.instance.url", "https://canvas.instructure.com");
            } else {
                this.apiToken = "";
                this.canvasInstanceUrl = "https://canvas.instructure.com";
            }
        } catch (Exception e) {
            System.err.println("Error loading Canvas configuration: " + e.getMessage());
            this.apiToken = "";
            this.canvasInstanceUrl = "https://canvas.instructure.com";
        }
    }
    
    /**
     * Sets the API token manually (useful for testing)
     */
    public void setApiToken(String token) {
        this.apiToken = token;
    }
    
    /**
     * Gets current user information from Canvas
     */
    public CompletableFuture<CanvasUserInfo> getCurrentUser() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(canvasInstanceUrl + "/api/v1/users/self"))
                        .header("Authorization", "Bearer " + apiToken)
                        .GET()
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), CanvasUserInfo.class);
                } else {
                    throw new RuntimeException("Failed to get user info: " + response.statusCode() + " - " + response.body());
                }
                
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Error getting user info", e);
            }
        });
    }
    
    /**
     * Gets user's enrolled courses from Canvas
     */
    public CompletableFuture<CanvasCourse[]> getUserCourses() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(canvasInstanceUrl + "/api/v1/courses?enrollment_state=active"))
                        .header("Authorization", "Bearer " + apiToken)
                        .GET()
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), CanvasCourse[].class);
                } else {
                    throw new RuntimeException("Failed to get courses: " + response.statusCode() + " - " + response.body());
                }
                
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Error getting courses", e);
            }
        });
    }
    
    /**
     * Gets calendar events from Canvas
     */
    public CompletableFuture<CanvasCalendarEvent[]> getCalendarEvents() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(canvasInstanceUrl + "/api/v1/calendar_events"))
                        .header("Authorization", "Bearer " + apiToken)
                        .GET()
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), CanvasCalendarEvent[].class);
                } else {
                    throw new RuntimeException("Failed to get calendar events: " + response.statusCode() + " - " + response.body());
                }
                
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Error getting calendar events", e);
            }
        });
    }
    
    /**
     * Tests the API connection
     */
    public CompletableFuture<Boolean> testConnection() {
        return getCurrentUser().thenApply(user -> {
            System.out.println("Canvas API connection successful! User: " + user.name);
            return true;
        }).exceptionally(throwable -> {
            System.err.println("Canvas API connection failed: " + throwable.getMessage());
            return false;
        });
    }
    
    /**
     * Inner class to represent Canvas user information
     */
    public static class CanvasUserInfo {
        public int id;
        public String name;
        public String short_name;
        public String sortable_name;
        public String login_id;
        public String email;
        public String avatar_url;
    }
    
    /**
     * Inner class to represent Canvas course information
     */
    public static class CanvasCourse {
        public int id;
        public String name;
        public String course_code;
        public String enrollment_state;
        public String workflow_state;
        public String start_at;
        public String end_at;
    }
    
    /**
     * Inner class to represent Canvas calendar event information
     */
    public static class CanvasCalendarEvent {
        public int id;
        public String title;
        public String description;
        public String start_at;
        public String end_at;
        public String location_name;
        public String context_code;
        public String workflow_state;
    }
}
