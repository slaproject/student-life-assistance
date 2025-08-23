package com.studentapp.frontend.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.io.InputStream;
import java.util.Properties;

public class CanvasOAuth2Client {
    
    // Canvas OAuth2 Configuration
    private String canvasAuthUrl;
    private String canvasTokenUrl;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope;
    private String canvasInstanceUrl;
    
    private final HttpClient httpClient;
    private final Gson gson;
    
    public CanvasOAuth2Client() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        loadConfiguration();
    }
    
    private void loadConfiguration() {
        try {
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("canvas-config.properties");
            if (input != null) {
                props.load(input);
                input.close();
                
                canvasAuthUrl = props.getProperty("canvas.auth.url", "https://canvas.instructure.com/login/oauth2/auth");
                canvasTokenUrl = props.getProperty("canvas.token.url", "https://canvas.instructure.com/login/oauth2/token");
                clientId = props.getProperty("canvas.client.id", "your_canvas_client_id");
                clientSecret = props.getProperty("canvas.client.secret", "your_canvas_client_secret");
                redirectUri = props.getProperty("canvas.redirect.uri", "http://localhost:8080/canvas/callback");
                scope = props.getProperty("canvas.scope", "url:GET|/api/v1/courses url:GET|/api/v1/users/self");
                canvasInstanceUrl = props.getProperty("canvas.instance.url", "https://canvas.instructure.com");
            } else {
                // Fallback to default values
                canvasAuthUrl = "https://canvas.instructure.com/login/oauth2/auth";
                canvasTokenUrl = "https://canvas.instructure.com/login/oauth2/token";
                clientId = "your_canvas_client_id";
                clientSecret = "your_canvas_client_secret";
                redirectUri = "http://localhost:8080/canvas/callback";
                scope = "url:GET|/api/v1/courses url:GET|/api/v1/users/self";
                canvasInstanceUrl = "https://canvas.instructure.com";
            }
        } catch (Exception e) {
            System.err.println("Error loading Canvas configuration: " + e.getMessage());
            // Use default values
            canvasAuthUrl = "https://canvas.instructure.com/login/oauth2/auth";
            canvasTokenUrl = "https://canvas.instructure.com/login/oauth2/token";
            clientId = "your_canvas_client_id";
            clientSecret = "your_canvas_client_secret";
            redirectUri = "http://localhost:8080/canvas/callback";
            scope = "url:GET|/api/v1/courses url:GET|/api/v1/users/self";
            canvasInstanceUrl = "https://canvas.instructure.com";
        }
    }
    
    /**
     * Initiates the Canvas OAuth2 authorization flow
     * @param stage The JavaFX stage to show the authorization dialog
     * @return CompletableFuture that completes with the access token
     */
    public CompletableFuture<String> authorize(Stage stage) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        try {
            // Build the authorization URL
            String authUrl = buildAuthorizationUrl();
            
            // Create a WebView to handle the OAuth flow
            WebView webView = new WebView();
            webView.getEngine().load(authUrl);
            
            // Handle the redirect to get the authorization code
            webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && newValue.contains("code=")) {
                    String code = extractAuthorizationCode(newValue);
                    if (code != null) {
                        // Exchange code for access token
                        exchangeCodeForToken(code).thenAccept(token -> {
                            Platform.runLater(() -> {
                                stage.close();
                                future.complete(token);
                            });
                        }).exceptionally(throwable -> {
                            Platform.runLater(() -> {
                                stage.close();
                                future.completeExceptionally(throwable);
                            });
                            return null;
                        });
                    }
                }
            });
            
            // Show the authorization dialog
            Stage authStage = new Stage();
            authStage.setTitle("Canvas LMS Authorization");
            authStage.setScene(new javafx.scene.Scene(webView, 800, 600));
            authStage.show();
            
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    /**
     * Builds the Canvas OAuth2 authorization URL
     */
    public String buildAuthorizationUrl() throws Exception {
        return canvasAuthUrl + "?" +
                "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8);
    }
    
    /**
     * Extracts the authorization code from the redirect URL
     */
    public String extractAuthorizationCode(String url) {
        if (url.contains("code=")) {
            String[] parts = url.split("code=");
            if (parts.length > 1) {
                String codePart = parts[1];
                if (codePart.contains("&")) {
                    return codePart.split("&")[0];
                }
                return codePart;
            }
        }
        return null;
    }
    
    /**
     * Exchanges the authorization code for an access token
     */
    private CompletableFuture<String> exchangeCodeForToken(String code) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String requestBody = "grant_type=authorization_code" +
                        "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                        "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8) +
                        "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                        "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);
                
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(canvasTokenUrl))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                    return jsonResponse.get("access_token").getAsString();
                } else {
                    throw new RuntimeException("Failed to exchange code for token: " + response.statusCode() + " - " + response.body());
                }
                
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Error exchanging code for token", e);
            }
        });
    }
    
    /**
     * Gets user information from Canvas using the access token
     */
    public CompletableFuture<CanvasUserInfo> getUserInfo(String accessToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(canvasInstanceUrl + "/api/v1/users/self"))
                        .header("Authorization", "Bearer " + accessToken)
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
     * Gets user's courses from Canvas
     */
    public CompletableFuture<CanvasCourse[]> getUserCourses(String accessToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(canvasInstanceUrl + "/api/v1/courses?enrollment_state=active"))
                        .header("Authorization", "Bearer " + accessToken)
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
}
