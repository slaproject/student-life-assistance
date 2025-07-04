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

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private String jwtToken;

    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isEmpty()) {
            errorLabel.setText("Username cannot be empty");
            return;
        }
        if (password == null || password.isEmpty()) {
            errorLabel.setText("Password cannot be empty");
            return;
        }

        String token = CalendarApiClient.login(username, password);
        System.out.println("Login Controller Token : " + token);
        if (token != null && !token.equals("Invalid username or password")) {
            this.jwtToken = token;
            errorLabel.setText("");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/main-view.fxml"));
                Parent mainRoot = loader.load();
                MainController mainController = loader.getController();
                mainController.setJwtToken(jwtToken);
                mainController.showCalendar();
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(mainRoot, 900, 700));
            } catch (Exception e) {
                errorLabel.setText("Failed to load main view: " + e.getMessage());
            }
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }

    public void goToSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/signup-view.fxml"));
            Parent signupRoot = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(signupRoot, 450, 450));
        } catch (Exception e) {
            errorLabel.setText("Failed to load signup view: " + e.getMessage());
        }
    }

    public String getJwtToken() {
        return jwtToken;
    }
}


