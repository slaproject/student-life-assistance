package com.studentapp.frontend.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class HelloController {
    
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
        showInfoAlert("Schedule", "View your class schedule, exam dates, and important academic deadlines.");
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
        return (Stage) getCurrentStage().getScene().getWindow();
    }
}