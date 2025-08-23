package com.studentapp.frontend.controller;

import com.studentapp.frontend.client.CanvasSsoClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;

public class CanvasSsoLoginController {
    
    @FXML
    private VBox canvasLoginContainer;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private Button detectInstitutionButton;
    
    @FXML
    private Button launchCanvasButton;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Label institutionLabel;
    
    private CanvasSsoClient canvasClient;
    private CanvasSsoClient.InstitutionInfo institutionInfo;
    private MainController mainController; // Reference to MainController
    
    @FXML
    public void initialize() {
        canvasClient = new CanvasSsoClient();
        statusLabel.setText("");
        institutionLabel.setText("");
        
        canvasClient.printInstitutionMappings();
        emailField.setPromptText("Enter your institution email (e.g., student@university.edu)");
        launchCanvasButton.setDisable(true);
    }
    
    /**
     * Set the MainController reference for embedding Canvas in main window
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    @FXML
    public void handleDetectInstitution() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            statusLabel.setText("Please enter your institution email");
            return;
        }
        
        if (!email.contains("@")) {
            statusLabel.setText("Please enter a valid email address");
            return;
        }
        
        // Show detecting status
        statusLabel.setText("Detecting your institution...");
        detectInstitutionButton.setDisable(true);
        
        // Debug: Test URL detection directly
        System.out.println("Testing URL detection for: " + email);
        canvasClient.testUrlDetection(email);
        
        // Test WebView loading
        canvasClient.testWebViewLoading(email).thenAccept(webView -> {
            System.out.println("WebView test completed successfully");
        }).exceptionally(throwable -> {
            System.err.println("WebView test failed: " + throwable.getMessage());
            return null;
        });
        
        // Detect institution from email
        canvasClient.getInstitutionInfo(email).thenAccept(info -> {
            Platform.runLater(() -> {
                this.institutionInfo = info;
                System.out.println("=== Institution Detection Result ===");
                System.out.println("Email: " + info.email);
                System.out.println("Domain: " + info.domain);
                System.out.println("Canvas URL: " + info.canvasUrl);
                System.out.println("Institution Name: " + info.institutionName);
                System.out.println("===================================");
                
                institutionLabel.setText("Institution: " + info.institutionName + " (" + info.canvasUrl + ")");
                statusLabel.setText("Institution detected! Click 'Launch Canvas' to continue.");
                launchCanvasButton.setDisable(false);
                detectInstitutionButton.setDisable(false);
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                statusLabel.setText("Error detecting institution: " + throwable.getMessage());
                detectInstitutionButton.setDisable(false);
            });
            return null;
        });
    }
    
    @FXML
    public void handleLaunchCanvas() {
        if (institutionInfo == null) {
            statusLabel.setText("Please detect your institution first");
            return;
        }
        
        // Show launching status
        statusLabel.setText("Launching Canvas...");
        launchCanvasButton.setDisable(true);
        
        // Launch Canvas in embedded WebView
        canvasClient.launchCanvasSso(getCurrentStage(), institutionInfo.email).thenAccept(webView -> {
            Platform.runLater(() -> {
                if (mainController != null) {
                    // Set the Canvas WebView as the center content of the main application with toolbar
                    mainController.setCanvasContent(webView);
                    
                    // No need to close the current stage since it's embedded in main window
                } else {
                    // Fallback: create a new stage if main controller not found
                    Stage canvasStage = new Stage();
                    canvasStage.setTitle("Canvas - " + institutionInfo.institutionName);
                    canvasStage.setScene(new Scene(webView, 1200, 800));
                    canvasStage.show();
                }
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                statusLabel.setText("Error launching Canvas: " + throwable.getMessage());
                launchCanvasButton.setDisable(false);
            });
            return null;
        });
    }
    

    
    @FXML
    public void handleEmailEntered() {
        // Auto-detect institution when user presses Enter in email field
        if (!emailField.getText().trim().isEmpty()) {
            handleDetectInstitution();
        }
    }
    
    @FXML
    public void handleCancel() {
        // Return to main view instead of closing dialog
        if (mainController != null) {
            mainController.showCalendar();
        }
    }
    
    private Stage getCurrentStage() {
        return (Stage) canvasLoginContainer.getScene().getWindow();
    }
    
    public CanvasSsoClient getCanvasClient() {
        return canvasClient;
    }
    
    public CanvasSsoClient.InstitutionInfo getInstitutionInfo() {
        return institutionInfo;
    }
}
