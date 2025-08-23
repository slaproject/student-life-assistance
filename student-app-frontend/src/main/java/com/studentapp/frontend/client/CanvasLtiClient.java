package com.studentapp.frontend.client;

import com.google.gson.Gson;
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
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CanvasLtiClient {
    
    private final HttpClient httpClient;
    private final Gson gson;
    private String canvasInstanceUrl;
    private String consumerKey;
    private String consumerSecret;
    private String launchUrl;
    
    public CanvasLtiClient() {
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
                
                this.canvasInstanceUrl = props.getProperty("canvas.instance.url", "https://canvas.instructure.com");
                this.consumerKey = props.getProperty("canvas.lti.consumer.key", "");
                this.consumerSecret = props.getProperty("canvas.lti.consumer.secret", "");
                this.launchUrl = props.getProperty("canvas.lti.launch.url", canvasInstanceUrl + "/courses");
            } else {
                this.canvasInstanceUrl = "https://canvas.instructure.com";
                this.consumerKey = "";
                this.consumerSecret = "";
                this.launchUrl = "https://canvas.instructure.com/courses";
            }
        } catch (Exception e) {
            System.err.println("Error loading Canvas LTI configuration: " + e.getMessage());
            this.canvasInstanceUrl = "https://canvas.instructure.com";
            this.consumerKey = "";
            this.consumerSecret = "";
            this.launchUrl = "https://canvas.instructure.com/courses";
        }
    }
    
    /**
     * Initiates LTI launch to embed Canvas in the application
     */
    public CompletableFuture<WebView> launchCanvasLti(Stage stage, String userEmail) {
        CompletableFuture<WebView> future = new CompletableFuture<>();
        
        try {
            // Create LTI launch parameters
            Map<String, String> ltiParams = createLtiLaunchParams(userEmail);
            
            // Build the LTI launch URL
            String launchUrl = buildLtiLaunchUrl(ltiParams);
            
            // Create WebView to display Canvas
            WebView webView = new WebView();
            webView.getEngine().load(launchUrl);
            
            // Handle navigation and communication
            setupWebViewHandlers(webView, stage);
            
            future.complete(webView);
            
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    /**
     * Creates LTI launch parameters
     */
    private Map<String, String> createLtiLaunchParams(String userEmail) {
        Map<String, String> params = new HashMap<>();
        
        // Required LTI parameters
        params.put("lti_message_type", "basic-lti-launch-request");
        params.put("lti_version", "LTI-1p0");
        params.put("resource_link_id", "student-life-assistance");
        params.put("resource_link_title", "Student Life Assistance");
        params.put("resource_link_description", "Access Canvas courses and assignments");
        
        // User information
        params.put("user_id", userEmail);
        params.put("lis_person_name_full", "Student User");
        params.put("lis_person_name_family", "User");
        params.put("lis_person_name_given", "Student");
        params.put("lis_person_email_primary", userEmail);
        
        // Context information
        params.put("context_id", "student-life-assistance-context");
        params.put("context_title", "Student Life Assistance");
        params.put("context_label", "SLA");
        
        // Tool consumer information
        params.put("tool_consumer_instance_guid", "student-life-assistance.com");
        params.put("tool_consumer_instance_name", "Student Life Assistance");
        params.put("tool_consumer_instance_description", "Student Life Management Application");
        
        // Launch presentation
        params.put("launch_presentation_document_target", "iframe");
        params.put("launch_presentation_return_url", "http://localhost:8080/canvas/return");
        params.put("launch_presentation_locale", "en");
        params.put("launch_presentation_width", "100%");
        params.put("launch_presentation_height", "600");
        
        // Roles
        params.put("roles", "Learner");
        
        // Timestamp and nonce
        params.put("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("oauth_nonce", generateNonce());
        
        return params;
    }
    
    /**
     * Builds the LTI launch URL with OAuth signature
     */
    private String buildLtiLaunchUrl(Map<String, String> params) throws Exception {
        // Add OAuth parameters
        params.put("oauth_consumer_key", consumerKey);
        params.put("oauth_signature_method", "HMAC-SHA1");
        params.put("oauth_version", "1.0");
        
        // Generate OAuth signature
        String signature = generateOAuthSignature(params, "POST", launchUrl);
        params.put("oauth_signature", signature);
        
        // Build the form data
        StringBuilder formData = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (formData.length() > 0) {
                formData.append("&");
            }
            formData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                   .append("=")
                   .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        
        return launchUrl + "?" + formData.toString();
    }
    
    /**
     * Generates OAuth signature for LTI launch
     */
    private String generateOAuthSignature(Map<String, String> params, String method, String url) throws Exception {
        // Sort parameters
        String sortedParams = params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce("", (a, b) -> a + "&" + b);
        
        // Create signature base string
        String signatureBaseString = method + "&" + 
                URLEncoder.encode(url, StandardCharsets.UTF_8) + "&" +
                URLEncoder.encode(sortedParams, StandardCharsets.UTF_8);
        
        // Create signing key
        String signingKey = URLEncoder.encode(consumerSecret, StandardCharsets.UTF_8) + "&";
        
        // Generate HMAC-SHA1 signature
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                signingKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        mac.init(secretKeySpec);
        
        byte[] signatureBytes = mac.doFinal(signatureBaseString.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signatureBytes);
    }
    
    /**
     * Generates a random nonce for OAuth
     */
    private String generateNonce() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * Sets up WebView handlers for communication
     */
    private void setupWebViewHandlers(WebView webView, Stage stage) {
        // Handle navigation events
        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Canvas LTI Navigation: " + newValue);
                
                // Handle specific Canvas URLs
                if (newValue.contains("/courses")) {
                    System.out.println("User navigated to courses");
                } else if (newValue.contains("/assignments")) {
                    System.out.println("User navigated to assignments");
                } else if (newValue.contains("/grades")) {
                    System.out.println("User navigated to grades");
                }
            }
        });
        
        // Handle JavaScript console messages
        webView.getEngine().setOnAlert(event -> {
            System.out.println("Canvas Alert: " + event.getData());
        });
        
        // Handle errors
        webView.getEngine().setOnError(event -> {
            System.err.println("Canvas WebView Error: " + event.getMessage());
        });
    }
    
    /**
     * Gets Canvas course information via LTI
     */
    public CompletableFuture<CanvasCourse[]> getCoursesViaLti(String userEmail) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // This would require additional LTI service calls
                // For now, return empty array as placeholder
                return new CanvasCourse[0];
            } catch (Exception e) {
                throw new RuntimeException("Error getting courses via LTI", e);
            }
        });
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
