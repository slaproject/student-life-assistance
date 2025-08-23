package com.studentapp.frontend.controller;

import com.studentapp.frontend.client.CanvasOAuth2Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;

public class CanvasLoginController {
    
    @FXML
    private VBox canvasLoginContainer;
    
    @FXML
    private Button canvasLoginButton;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private ProgressIndicator progressIndicator;
    
    private CanvasOAuth2Client canvasClient;
    private String canvasAccessToken;
    private CanvasOAuth2Client.CanvasUserInfo canvasUserInfo;
    
    @FXML
    public void initialize() {
        canvasClient = new CanvasOAuth2Client();
        progressIndicator.setVisible(false);
        statusLabel.setText("");
    }
    
    @FXML
    public void handleCanvasLogin() {
        if (canvasClient == null) {
            canvasClient = new CanvasOAuth2Client();
        }
        
        // Show progress and disable button
        progressIndicator.setVisible(true);
        canvasLoginButton.setDisable(true);
        statusLabel.setText("Connecting to Canvas LMS...");
        
        Stage currentStage = (Stage) canvasLoginContainer.getScene().getWindow();
        
        // Start the OAuth2 flow
        CompletableFuture<String> authFuture = canvasClient.authorize(currentStage);
        
        authFuture.thenAccept(accessToken -> {
            Platform.runLater(() -> {
                this.canvasAccessToken = accessToken;
                statusLabel.setText("Successfully authenticated with Canvas!");
                
                // Get user information
                canvasClient.getUserInfo(accessToken).thenAccept(userInfo -> {
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
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                statusLabel.setText("Authentication failed: " + throwable.getMessage());
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
            mainController.setCanvasAccessToken(canvasAccessToken);
            mainController.setCanvasUserInfo(canvasUserInfo);
            
            // Show calendar view
            mainController.showCalendar();
            
            Stage stage = (Stage) canvasLoginContainer.getScene().getWindow();
            stage.setScene(new Scene(mainRoot, 900, 700));
            
        } catch (Exception e) {
            statusLabel.setText("Error loading main view: " + e.getMessage());
            progressIndicator.setVisible(false);
            canvasLoginButton.setDisable(false);
        }
    }
    
    public String getCanvasAccessToken() {
        return canvasAccessToken;
    }
    
    public CanvasOAuth2Client.CanvasUserInfo getCanvasUserInfo() {
        return canvasUserInfo;
    }
}
