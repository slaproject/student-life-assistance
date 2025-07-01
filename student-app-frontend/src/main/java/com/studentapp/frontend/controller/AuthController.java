package com.studentapp.frontend.controller;

import com.studentapp.frontend.client.CalendarApiClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AuthController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    private String jwtToken;

    public void handleSignup() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String response = CalendarApiClient.signup(username, email, password);
        showAlert("Signup", response);

        // Reset fields after signup
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
    }

    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String token = CalendarApiClient.login(username, password);
        if (token != null && !token.equals("Invalid username or password")) {
            this.jwtToken = token;
            showAlert("Login", "Login successful!");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/main-view.fxml"));
                Parent mainRoot = loader.load();
                com.studentapp.frontend.controller.HelloController helloController = loader.getController();
                helloController.showCalendar();
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(mainRoot, 900, 700));
            } catch (Exception e) {
                showAlert("Error", "Failed to load main view: " + e.getMessage());
            }
        } else {
            showAlert("Login", "Invalid username or password");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public String getJwtToken() {
        return jwtToken;
    }
} 