package com.studentapp.frontend.controller;

import com.studentapp.frontend.client.CalendarApiClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupController {
    public interface SignupService {
        String signup(String username, String email, String password);
    }

    private SignupService signupService = CalendarApiClient::signup;

    // For testing
    public void setSignupService(SignupService signupService) {
        this.signupService = signupService;
    }

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    // Helper to sanitize input by removing HTML tags
    private String sanitizeInput(String input) {
        return input == null ? null : input.replaceAll("<[^>]*>", "");
    }

    public void handleSignup() {
        String username = sanitizeInput(usernameField.getText());
        String email = sanitizeInput(emailField.getText());
        String password = sanitizeInput(passwordField.getText());

        if (username == null || username.isEmpty()) {
            errorLabel.setText("Username cannot be empty");
            return;
        }
        if (email == null || email.isEmpty() || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            errorLabel.setText("Please enter a valid email address");
            return;
        }
        if (password == null || password.isEmpty()) {
            errorLabel.setText("Password cannot be empty");
            return;
        }
        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters long");
            return;
        }
        if (!password.matches(".*\\d.*")) {
            errorLabel.setText("Password must contain at least one number");
            return;
        }

        String response = signupService.signup(username, email, password);
        if (response == null) {
            errorLabel.setText("Signup failed. Please try again.");
        } else if (response.equalsIgnoreCase("User registered successfully")) {
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Signup successful! You can now log in.");
            usernameField.clear();
            emailField.clear();
            passwordField.clear();
        } else if (response.equalsIgnoreCase("Username already exists")) {
            errorLabel.setText("The username '" + username + "' is already taken.");
        } else if (response.equalsIgnoreCase("Email already exists")) {
            errorLabel.setText("The email '" + email + "' is already registered.");
        } else if (response.toLowerCase().contains("password must be at least 6 characters")) {
            errorLabel.setText("Password must be at least 6 characters and contain at least one number.");
        } else {
            errorLabel.setText(response);
        }
    }

    public void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/login-view.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loginRoot, 450, 400));
        } catch (Exception e) {
            errorLabel.setText("Failed to load login view: " + e.getMessage());
        }
    }
}
