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

import java.io.File;

public class MainController {
    @FXML
    private BorderPane rootPane;

    private CalendarApiClient apiClient = new CalendarApiClient();
    private CalendarView calendarView;
    private String jwtToken;
    private CalendarController calendarController;

    public void setCenterContent(Node node) {
        rootPane.setCenter(node);
    }

    // File Menu Actions
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
    
    // Student Menu Actions
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
    
    // Finance Menu Actions
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
    
    // Resources Menu Actions
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
    
    // Help Menu Actions
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
    
    // Helper methods
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
    
    private Stage getCurrentStage() {
        // Get the stage from the scene of any node
        try {
            // Try to get stage from the scene
            return (Stage) javafx.scene.Node.class.cast(this).getScene().getWindow();
        } catch (Exception e) {
            // Alternative approach: get from any visible window
            for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                if (window instanceof Stage && window.isShowing()) {
                    return (Stage) window;
                }
            }
            return null;
        }
    }

    public void setJwtToken(String token) {
        this.jwtToken = token;
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
            System.out.println("Main Controller: "+jwtToken);
            calendarController.setJwtToken(jwtToken);
            setCenterContent(calendarRoot);
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

            // Clear the JWT token in MainController
            this.jwtToken = null;

            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/login-view.fxml"));
            Parent loginRoot = loader.load();

            // Set the login view in the current stage
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
}