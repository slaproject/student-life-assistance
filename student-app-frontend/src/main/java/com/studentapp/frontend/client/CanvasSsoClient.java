package com.studentapp.frontend.client;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import java.util.Map;

public class CanvasSsoClient {
    
    private final HttpClient httpClient;
    private final Gson gson;
    private Map<String, String> institutionDomains;
    
    public CanvasSsoClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        initializeInstitutionDomains();
    }
    
    /**
     * Initialize common institution domains and their Canvas URLs
     */
    private void initializeInstitutionDomains() {
        institutionDomains = new HashMap<>();
        
        // Common university domains - Updated with more accurate URLs
        institutionDomains.put("edu", "https://canvas.instructure.com");
        institutionDomains.put("harvard.edu", "https://canvas.harvard.edu");
        institutionDomains.put("mit.edu", "https://canvas.mit.edu");
        institutionDomains.put("stanford.edu", "https://canvas.stanford.edu");
        institutionDomains.put("berkeley.edu", "https://bcourses.berkeley.edu");
        institutionDomains.put("ucla.edu", "https://ccle.ucla.edu");
        institutionDomains.put("usc.edu", "https://blackboard.usc.edu");
        institutionDomains.put("nyu.edu", "https://classes.nyu.edu");
        institutionDomains.put("columbia.edu", "https://courseworks.columbia.edu");
        institutionDomains.put("yale.edu", "https://canvas.yale.edu");
        institutionDomains.put("princeton.edu", "https://canvas.princeton.edu");
        institutionDomains.put("dartmouth.edu", "https://canvas.dartmouth.edu");
        institutionDomains.put("brown.edu", "https://canvas.brown.edu");
        institutionDomains.put("cornell.edu", "https://canvas.cornell.edu");
        institutionDomains.put("penn.edu", "https://canvas.upenn.edu");
        
        // Add more common institutions
        institutionDomains.put("utexas.edu", "https://utexas.instructure.com");
        institutionDomains.put("umich.edu", "https://umich.instructure.com");
        institutionDomains.put("uiuc.edu", "https://uiuc.instructure.com");
        institutionDomains.put("gatech.edu", "https://gatech.instructure.com");
        institutionDomains.put("cmu.edu", "https://cmu.instructure.com");
        institutionDomains.put("caltech.edu", "https://caltech.instructure.com");
        institutionDomains.put("rice.edu", "https://rice.instructure.com");
        institutionDomains.put("vanderbilt.edu", "https://vanderbilt.instructure.com");
        institutionDomains.put("emory.edu", "https://emory.instructure.com");
        institutionDomains.put("duke.edu", "https://duke.instructure.com");
        institutionDomains.put("unc.edu", "https://unc.instructure.com");
        institutionDomains.put("ncsu.edu", "https://ncsu.instructure.com");
        institutionDomains.put("virginia.edu", "https://virginia.instructure.com");
        institutionDomains.put("vt.edu", "https://vt.instructure.com");
        institutionDomains.put("georgetown.edu", "https://georgetown.instructure.com");
        institutionDomains.put("georgiatech.edu", "https://gatech.instructure.com");
        institutionDomains.put("gatech.edu", "https://gatech.instructure.com");
        institutionDomains.put("northeastern.edu", "https://northeastern.instructure.com");
        institutionDomains.put("bu.edu", "https://bu.instructure.com");
        institutionDomains.put("tufts.edu", "https://tufts.instructure.com");
        institutionDomains.put("brandeis.edu", "https://brandeis.instructure.com");
        institutionDomains.put("bostoncollege.edu", "https://bc.instructure.com");
        institutionDomains.put("bc.edu", "https://bc.instructure.com");
        institutionDomains.put("umass.edu", "https://umass.instructure.com");
        institutionDomains.put("umassd.edu", "https://umassd.instructure.com");
        institutionDomains.put("wpi.edu", "https://wpi.instructure.com");
        institutionDomains.put("worcester.edu", "https://worcester.instructure.com");
        institutionDomains.put("clarku.edu", "https://clarku.instructure.com");
        institutionDomains.put("holycross.edu", "https://holycross.instructure.com");
        institutionDomains.put("assumption.edu", "https://assumption.instructure.com");
        institutionDomains.put("bentley.edu", "https://bentley.instructure.com");
        institutionDomains.put("suffolk.edu", "https://suffolk.instructure.com");
        institutionDomains.put("simmons.edu", "https://simmons.instructure.com");
        institutionDomains.put("emerson.edu", "https://emerson.instructure.com");
        institutionDomains.put("berklee.edu", "https://berklee.instructure.com");
        institutionDomains.put("massart.edu", "https://massart.instructure.com");
        institutionDomains.put("mfa.edu", "https://mfa.instructure.com");
    }
    
    /**
     * Detects Canvas URL from email domain
     */
    public String detectCanvasUrl(String email) {
        if (email == null || !email.contains("@")) {
            System.out.println("Invalid email format: " + email);
            return "https://canvas.instructure.com"; // Default fallback
        }
        
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        System.out.println("Extracted domain: " + domain);
        
        // Check for exact domain match
        if (institutionDomains.containsKey(domain)) {
            String canvasUrl = institutionDomains.get(domain);
            System.out.println("Found exact match for " + domain + ": " + canvasUrl);
            return canvasUrl;
        }
        
        // Check for subdomain patterns
        for (Map.Entry<String, String> entry : institutionDomains.entrySet()) {
            if (domain.endsWith("." + entry.getKey())) {
                String canvasUrl = entry.getValue();
                System.out.println("Found subdomain match for " + domain + " -> " + entry.getKey() + ": " + canvasUrl);
                return canvasUrl;
            }
        }
        
        // If no match found, try to construct a common pattern
        if (domain.endsWith(".edu")) {
            String institution = domain.replace(".edu", "");
            String canvasUrl = "https://" + institution + ".instructure.com";
            System.out.println("Constructed URL for " + domain + ": " + canvasUrl);
            return canvasUrl;
        }
        
        System.out.println("No match found for domain: " + domain + ", using default");
        return "https://canvas.instructure.com"; // Default fallback
    }
    
    /**
     * Launches Canvas SSO for the given email
     */
    public CompletableFuture<WebView> launchCanvasSso(Stage stage, String email) {
        CompletableFuture<WebView> future = new CompletableFuture<>();
        
        try {
            System.out.println("=== Canvas SSO Launch Debug ===");
            System.out.println("Input email: " + email);
            
            // Detect Canvas URL from email
            String canvasUrl = detectCanvasUrl(email);
            System.out.println("Final Canvas URL: " + canvasUrl);
            System.out.println("===============================");
            
            // Create WebView to display Canvas
            WebView webView = new WebView();
            
            // Load the Canvas login page
            System.out.println("Loading WebView with URL: " + canvasUrl);
            
            // Force the WebView to load the specific institution URL
            webView.getEngine().load(canvasUrl);
            
            // Add a listener to verify the URL was loaded
            webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("WebView location changed from: " + oldValue + " to: " + newValue);
                if (newValue != null && !newValue.equals(canvasUrl)) {
                    System.out.println("WARNING: URL was redirected from " + canvasUrl + " to " + newValue);
                }
            });
            
            // Set up handlers for the WebView
            setupWebViewHandlers(webView, stage, email);
            
            future.complete(webView);
            
        } catch (Exception e) {
            System.err.println("Error in launchCanvasSso: " + e.getMessage());
            e.printStackTrace();
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    /**
     * Sets up WebView handlers for Canvas integration
     */
    private void setupWebViewHandlers(WebView webView, Stage stage, String email) {
        // Handle navigation events
        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Canvas Navigation: " + newValue);
                
                // Check if user has successfully logged in
                if (newValue.contains("/dashboard") || newValue.contains("/courses")) {
                    System.out.println("User successfully logged into Canvas!");
                    Platform.runLater(() -> {
                        // You can add custom logic here when user logs in
                        // For example, show a success message or update UI
                    });
                }
                
                // Handle specific Canvas sections
                if (newValue.contains("/courses")) {
                    System.out.println("User navigated to courses");
                } else if (newValue.contains("/assignments")) {
                    System.out.println("User navigated to assignments");
                } else if (newValue.contains("/grades")) {
                    System.out.println("User navigated to grades");
                } else if (newValue.contains("/calendar")) {
                    System.out.println("User navigated to calendar");
                }
            }
        });
        
        // Handle load state changes
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("WebView Load State: " + newValue);
            if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
                System.out.println("WebView loaded successfully. Current URL: " + webView.getEngine().getLocation());
            } else if (newValue == javafx.concurrent.Worker.State.FAILED) {
                System.err.println("WebView failed to load: " + webView.getEngine().getLoadWorker().getException());
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
        
        // Handle load state changes
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Canvas Load State: " + newValue);
        });
    }
    
    /**
     * Gets institution information for an email
     */
    public CompletableFuture<InstitutionInfo> getInstitutionInfo(String email) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String canvasUrl = detectCanvasUrl(email);
                String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
                
                InstitutionInfo info = new InstitutionInfo();
                info.email = email;
                info.domain = domain;
                info.canvasUrl = canvasUrl;
                info.institutionName = getFriendlyInstitutionName(domain);
                
                return info;
            } catch (Exception e) {
                throw new RuntimeException("Error getting institution info", e);
            }
        });
    }
    
    /**
     * Extracts institution name from domain
     */
    private String extractInstitutionName(String domain) {
        if (domain.endsWith(".edu")) {
            String name = domain.replace(".edu", "");
            // Convert to title case
            String[] words = name.split("\\.");
            StringBuilder result = new StringBuilder();
            for (String word : words) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                if (word.length() > 0) {
                    result.append(word.substring(0, 1).toUpperCase())
                          .append(word.substring(1).toLowerCase());
                }
            }
            return result.toString();
        }
        return "Unknown Institution";
    }
    
    /**
     * Gets a more user-friendly institution name
     */
    private String getFriendlyInstitutionName(String domain) {
        switch (domain.toLowerCase()) {
            case "northeastern.edu":
                return "Northeastern University";
            case "harvard.edu":
                return "Harvard University";
            case "mit.edu":
                return "Massachusetts Institute of Technology";
            case "stanford.edu":
                return "Stanford University";
            case "berkeley.edu":
                return "University of California, Berkeley";
            case "ucla.edu":
                return "University of California, Los Angeles";
            case "usc.edu":
                return "University of Southern California";
            case "nyu.edu":
                return "New York University";
            case "columbia.edu":
                return "Columbia University";
            case "yale.edu":
                return "Yale University";
            case "princeton.edu":
                return "Princeton University";
            case "dartmouth.edu":
                return "Dartmouth College";
            case "brown.edu":
                return "Brown University";
            case "cornell.edu":
                return "Cornell University";
            case "penn.edu":
                return "University of Pennsylvania";
            case "bu.edu":
                return "Boston University";
            case "tufts.edu":
                return "Tufts University";
            case "brandeis.edu":
                return "Brandeis University";
            case "bc.edu":
            case "bostoncollege.edu":
                return "Boston College";
            case "umass.edu":
                return "University of Massachusetts";
            default:
                return extractInstitutionName(domain);
        }
    }
    
    /**
     * Adds a custom institution domain mapping
     */
    public void addInstitutionDomain(String domain, String canvasUrl) {
        institutionDomains.put(domain.toLowerCase(), canvasUrl);
    }
    
    /**
     * Test method to debug URL detection
     */
    public void testUrlDetection(String email) {
        System.out.println("=== Testing URL Detection ===");
        System.out.println("Email: " + email);
        String url = detectCanvasUrl(email);
        System.out.println("Detected URL: " + url);
        System.out.println("=============================");
    }
    
    /**
     * Debug method to print all available institution mappings
     */
    public void printInstitutionMappings() {
        System.out.println("=== Available Institution Mappings ===");
        for (Map.Entry<String, String> entry : institutionDomains.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println("=====================================");
    }
    
    /**
     * Test method to verify WebView loads the correct URL
     */
    public CompletableFuture<WebView> testWebViewLoading(String email) {
        CompletableFuture<WebView> future = new CompletableFuture<>();
        
        try {
            String canvasUrl = detectCanvasUrl(email);
            System.out.println("Testing WebView loading for: " + canvasUrl);
            
            WebView webView = new WebView();
            webView.getEngine().load(canvasUrl);
            
            // Monitor the loading
            webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("Test WebView Load State: " + newValue);
                if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
                    String currentUrl = webView.getEngine().getLocation();
                    System.out.println("Test WebView loaded successfully. Current URL: " + currentUrl);
                    System.out.println("Expected URL: " + canvasUrl);
                    System.out.println("URLs match: " + currentUrl.equals(canvasUrl));
                    future.complete(webView);
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error in testWebViewLoading: " + e.getMessage());
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    /**
     * Inner class to represent institution information
     */
    public static class InstitutionInfo {
        public String email;
        public String domain;
        public String canvasUrl;
        public String institutionName;
    }
}
