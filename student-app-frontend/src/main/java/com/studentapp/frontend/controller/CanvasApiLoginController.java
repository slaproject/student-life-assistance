package com.studentapp.frontend.controller;

import com.studentapp.frontend.client.CanvasApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;

public class CanvasApiLoginController {
    
    @FXML
    private VBox canvasLoginContainer;
    
    @FXML
    private TextField apiTokenField;
    
    @FXML
    private Button canvasLoginButton;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private ProgressIndicator progressIndicator;
    
    private CanvasApiClient canvasClient;
    private CanvasApiClient.CanvasUserInfo canvasUserInfo;
    
    @FXML
    public void initialize() {
        canvasClient = new CanvasApiClient();
        progressIndicator.setVisible(false);
        statusLabel.setText("");
        
        // Set placeholder text for the token field
        apiTokenField.setPromptText("Enter your Canvas API token");
    }
    
    @FXML
    public void handleCanvasLogin() {
        String apiToken = apiTokenField.getText().trim();
        
        if (apiToken.isEmpty()) {
            statusLabel.setText("Please enter your Canvas API token");
            return;
        }
        
        // Show progress and disable button
        progressIndicator.setVisible(true);
        canvasLoginButton.setDisable(true);
        statusLabel.setText("Connecting to Canvas...");
        
        // Set the API token
        canvasClient.setApiToken(apiToken);
        
        // Test the connection and get user info
        canvasClient.testConnection().thenAccept(success -> {
            if (success) {
                canvasClient.getCurrentUser().thenAccept(userInfo -> {
                    Platform.runLater(() -> {
                        this.canvasUserInfo = userInfo;
                        statusLabel.setText("Welcome, " + userInfo.name + "!");
                        
                        // Navigate to main view with Canvas integration
                        navigateToMainView();
                    });
                }).exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        statusLabel.setText("Error getting user info: " + throwable.getMessage());
                        progressIndicator.setVisible(false);
                        canvasLoginButton.setDisable(false);
                    });
                    return null;
                });
            } else {
                Platform.runLater(() -> {
                    statusLabel.setText("Invalid API token. Please check your token and try again.");
                    progressIndicator.setVisible(false);
                    canvasLoginButton.setDisable(false);
                });
            }
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                statusLabel.setText("Connection failed: " + throwable.getMessage());
                progressIndicator.setVisible(false);
                canvasLoginButton.setDisable(false);
            });
            return null;
        });
    }
    
    @FXML
    public void goBackToRegularLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/login-view.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) canvasLoginContainer.getScene().getWindow();
            stage.setScene(new Scene(loginRoot, 450, 400));
        } catch (Exception e) {
            statusLabel.setText("Error loading login view: " + e.getMessage());
        }
    }
    
    private void navigateToMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/main-view.fxml"));
            Parent mainRoot = loader.load();
            MainController mainController = loader.getController();
            
            // Set Canvas information in the main controller
            mainController.setCanvasApiClient(canvasClient);
            mainController.setCanvasUserInfo(canvasUserInfo);
            
            Stage stage = (Stage) canvasLoginContainer.getScene().getWindow();
            stage.setScene(new Scene(mainRoot, 900, 700));
            
            // Use Platform.runLater to ensure scene is ready before showing calendar
            Platform.runLater(() -> mainController.showCalendar());
            
        } catch (Exception e) {
            statusLabel.setText("Error loading main view: " + e.getMessage());
            progressIndicator.setVisible(false);
            canvasLoginButton.setDisable(false);
        }
    }
    
    public CanvasApiClient getCanvasClient() {
        return canvasClient;
    }
    
    public CanvasApiClient.CanvasUserInfo getCanvasUserInfo() {
        return canvasUserInfo;
    }
}
