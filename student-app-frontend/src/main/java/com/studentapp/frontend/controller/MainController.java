package com.studentapp.frontend.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import com.studentapp.frontend.view.CalendarView;
import com.studentapp.frontend.client.CalendarApiClient;
import com.studentapp.frontend.client.CanvasOAuth2Client;
import com.studentapp.frontend.client.CanvasApiClient;
import com.studentapp.frontend.client.CanvasSsoClient;
import javafx.scene.web.WebView;

import java.io.File;

public class MainController {
    @FXML
    private BorderPane rootPane;

    private CalendarApiClient apiClient = new CalendarApiClient();
    private CalendarView calendarView;
    private String jwtToken;
    private CalendarController calendarController;
    private CanvasSsoClient canvasSsoClient;
    
    // Canvas integration
    private String canvasAccessToken;
    private CanvasOAuth2Client.CanvasUserInfo canvasUserInfo;
    private CanvasApiClient canvasApiClient;
    private CanvasApiClient.CanvasUserInfo canvasApiUserInfo;

    public void setCenterContent(Node node) {
        rootPane.setCenter(node);
    }
    
    /**
     * Set Canvas WebView as center content with navigation toolbar
     */
    public void setCanvasContent(WebView webView) {
        // Create a VBox to hold the toolbar and WebView
        javafx.scene.layout.VBox container = new javafx.scene.layout.VBox();
        
        // Create toolbar
        javafx.scene.control.ToolBar toolbar = new javafx.scene.control.ToolBar();
        
        // Add return button
        javafx.scene.control.Button returnButton = new javafx.scene.control.Button("← Return to Main View");
        returnButton.setOnAction(e -> showCalendar());
        
        // Add Canvas title label
        javafx.scene.control.Label canvasLabel = new javafx.scene.control.Label("Canvas LMS");
        canvasLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Add spacer to push title to center
        javafx.scene.control.Separator separator = new javafx.scene.control.Separator();
        separator.setOrientation(javafx.geometry.Orientation.VERTICAL);
        
        toolbar.getItems().addAll(returnButton, separator, canvasLabel);
        
        // Set up the container
        container.getChildren().addAll(toolbar, webView);
        javafx.scene.layout.VBox.setVgrow(webView, javafx.scene.layout.Priority.ALWAYS);
        
        // Set the container as center content
        rootPane.setCenter(container);
    }

    @FXML
    private void handleNewAction() {
        showInfoAlert("New", "Create new student profile or document");
    }
    
    @FXML
    private void handleOpenAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Student File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        
        File selectedFile = fileChooser.showOpenDialog(getCurrentStage());
        if (selectedFile != null) {
            showInfoAlert("File Opened", "Opened file: " + selectedFile.getName());
        }
    }
    
    @FXML
    private void handleSaveAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Student Data");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        
        File selectedFile = fileChooser.showSaveDialog(getCurrentStage());
        if (selectedFile != null) {
            showInfoAlert("File Saved", "Saved to: " + selectedFile.getName());
        }
    }
    
    @FXML
    private void handleExitAction() {
        showConfirmationAlert("Exit", "Are you sure you want to exit?", () -> Platform.exit());
    }
    
    @FXML
    private void handleAcademicRecordsAction() {
        showInfoAlert("Academic Records", "View and manage your academic records, transcripts, and course history.");
    }
    
    @FXML
    private void handleScheduleAction() {
        showCalendar();
    }
    
    @FXML
    private void handleAssignmentsAction() {
        showInfoAlert("Assignments", "Track your assignments, projects, and submission deadlines.");
    }
    
    @FXML
    private void handleGradesAction() {
        showInfoAlert("Grades", "View your current grades, GPA, and academic performance.");
    }
    
    @FXML
    private void handleBudgetAction() {
        showInfoAlert("Budget Tracker", "Track your monthly budget, expenses, and financial planning.");
    }
    
    @FXML
    private void handleExpensesAction() {
        showInfoAlert("Expenses", "Log and categorize your daily expenses and spending patterns.");
    }
    
    @FXML
    private void handleFinancialAidAction() {
        showInfoAlert("Financial Aid", "Information about scholarships, grants, and financial aid opportunities.");
    }
    
    @FXML
    private void handleLibraryAction() {
        showInfoAlert("Library", "Access library resources, research databases, and study materials.");
    }
    
    @FXML
    private void handleCampusMapAction() {
        showInfoAlert("Campus Map", "Interactive campus map with building locations and directions.");
    }
    
    @FXML
    private void handleStudentServicesAction() {
        showInfoAlert("Student Services", "Access to counseling, health services, career guidance, and more.");
    }
    
    @FXML
    private void handleUserGuideAction() {
        showInfoAlert("User Guide", "Comprehensive guide on how to use the Student Life Assistant application.");
    }
    
    @FXML
    private void handleAboutAction() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About Student Life Assistant");
        alert.setHeaderText("Student Life Assistant v1.0.0");
        alert.setContentText("A comprehensive desktop application designed to help students manage their academic life, finances, and access campus resources.\n\n" +
                           "Features:\n" +
                           "• Academic record management\n" +
                           "• Financial planning and budgeting\n" +
                           "• Campus resource access\n" +
                           "• Schedule and assignment tracking\n\n" +
                           "Developed for better student life management.");
        alert.showAndWait();
    }
    
    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showConfirmationAlert(String title, String content, Runnable onConfirm) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                onConfirm.run();
            }
        });
    }
    
    /**
     * Get the current stage
     */
    private Stage getCurrentStage() {
        return (Stage) rootPane.getScene().getWindow();
    }
    
    public void setJwtToken(String token) {
        this.jwtToken = token;
    }
    
    public void setCanvasAccessToken(String token) {
        this.canvasAccessToken = token;
    }
    
    public void setCanvasUserInfo(CanvasOAuth2Client.CanvasUserInfo userInfo) {
        this.canvasUserInfo = userInfo;
    }
    
    public String getCanvasAccessToken() {
        return canvasAccessToken;
    }
    
    public CanvasOAuth2Client.CanvasUserInfo getCanvasUserInfo() {
        return canvasUserInfo;
    }
    
    public void setCanvasApiClient(CanvasApiClient client) {
        this.canvasApiClient = client;
    }
    
    public void setCanvasUserInfo(CanvasApiClient.CanvasUserInfo userInfo) {
        this.canvasApiUserInfo = userInfo;
    }
    
    public CanvasApiClient getCanvasApiClient() {
        return canvasApiClient;
    }
    
    public CanvasApiClient.CanvasUserInfo getCanvasApiUserInfo() {
        return canvasApiUserInfo;
    }

    @FXML
    public void initialize() {
        showCalendar();
    }

    public void showCalendar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/calendar-view.fxml"));
            Node calendarRoot = loader.load();
            calendarController = loader.getController();
            calendarController.setJwtToken(jwtToken);
            setCenterContent(calendarRoot);
            
            // Restore original title
            Stage currentStage = getCurrentStage();
            if (currentStage != null) {
                currentStage.setTitle("Student Life Assistance");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showInfoAlert("Error", "Failed to load calendar view: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogoutAction() {
        try {
            // Clear the JWT token in CalendarController
            if (calendarController != null) {
                calendarController.clearJwtToken();
            }

            this.jwtToken = null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/login-view.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(new Scene(loginRoot));
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showInfoAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }

    @FXML
    private void handleTimerAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/timer-view.fxml"));
            Parent timerRoot = loader.load();
            setCenterContent(timerRoot);
        } catch (Exception e) {
            showInfoAlert("Timer", "Failed to load Timer page: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleReturnToMainAction() {
        showCalendar();
    }
    
    @FXML
    private void handleCanvasAction() {
        try {
            System.out.println("=== Canvas Action Debug ===");
            System.out.println("handleCanvasAction called");
            
            // Initialize Canvas SSO client if not already done
            if (canvasSsoClient == null) {
                canvasSsoClient = new CanvasSsoClient();
                System.out.println("Canvas SSO client initialized");
            }
            
            // Show Canvas SSO login dialog
            System.out.println("Calling showCanvasLoginDialog");
            showCanvasLoginDialog();
            System.out.println("showCanvasLoginDialog completed");
            
        } catch (Exception e) {
            e.printStackTrace();
            showInfoAlert("Canvas Error", "Failed to launch Canvas: " + e.getMessage());
        }
    }
    
    /**
     * Show Canvas login view in the main window
     */
    public void showCanvasLogin() {
        showCanvasLoginDialog();
    }
    
    private void showCanvasLoginDialog() {
        try {
            System.out.println("=== showCanvasLoginDialog Debug ===");
            System.out.println("Loading canvas-sso-login-view.fxml");
            
            // Check if we're in the main window
            Stage currentStage = getCurrentStage();
            System.out.println("Current stage: " + currentStage);
            System.out.println("Current stage title: " + (currentStage != null ? currentStage.getTitle() : "null"));
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/canvas-sso-login-view.fxml"));
            Parent canvasLoginRoot = loader.load();
            System.out.println("FXML loaded successfully");
            
            CanvasSsoLoginController canvasController = loader.getController();
            System.out.println("Canvas controller obtained: " + canvasController);
            
            // Pass reference to MainController so Canvas can be embedded in main window
            canvasController.setMainController(this);
            System.out.println("MainController reference set");
            
            // Load Canvas login view directly into the main window's center content
            System.out.println("Setting center content with Canvas login view");
            setCenterContent(canvasLoginRoot);
            System.out.println("Center content set successfully");
            
            // Update the stage title to indicate Canvas mode
            if (currentStage != null) {
                currentStage.setTitle("Student Life Assistance - Canvas LMS");
            }
            
        } catch (Exception e) {
            System.err.println("Error in showCanvasLoginDialog: " + e.getMessage());
            e.printStackTrace();
            showInfoAlert("Canvas Error", "Failed to load Canvas login: " + e.getMessage());
        }
    }
}