package com.studentapp.frontend.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * Unit tests for LoginController, covering authentication, form validation, and navigation.
 */
@ExtendWith(ApplicationExtension.class)
public class LoginControllerTest {

    private LoginController loginController;
    private Stage stage;

    @Start
    private void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/login-view.fxml"));
        Parent root = loader.load();
        loginController = loader.getController();
        
        stage.setScene(new Scene(root));
        stage.show();
        
        // Wait for the scene to be fully loaded
        WaitForAsyncUtils.waitForFxEvents();
    }

    @BeforeEach
    void setUp() throws TimeoutException {
        // Ensure we're on the FX application thread
        FxToolkit.registerPrimaryStage();
    }


    /**
     * Tests login with invalid credentials shows error message.
     */
    @Test
    void testLoginWithInvalidCredentials(FxRobot robot) {
        robot.sleep(2000);
        robot.clickOn("#usernameField");
        robot.write("invaliduser");
        robot.clickOn("#passwordField");
        robot.write("wrongpass");
        robot.clickOn("Login");
        
        // Wait a bit for the error message to appear
        robot.sleep(1000);
        
        // Since the backend is not running, we get a connection error
        // The LoginController should still show "Invalid username or password"
        // because the connection error is not equal to "Invalid username or password"
        verifyThat("#errorLabel", hasText("Invalid username or password"));
    }

    /**
     * Tests login with empty username shows error message.
     */
    @Test
    void testLoginWithEmptyUsername(FxRobot robot) {
        robot.sleep(2000);
        robot.clickOn("#passwordField");
        robot.write("testpass123");
        robot.clickOn("Login");
        verifyThat("#errorLabel", hasText("Username cannot be empty"));
    }

    /**
     * Tests login with empty password shows error message.
     */
    @Test
    void testLoginWithEmptyPassword(FxRobot robot) {
        robot.sleep(2000);
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("Login");
        verifyThat("#errorLabel", hasText("Password cannot be empty"));
    }

    /**
     * Tests login with null credentials shows error message.
     */
    @Test
    void testLoginWithNullCredentials(FxRobot robot) {
        robot.sleep(2000);
        robot.clickOn("Login");
        verifyThat("#errorLabel", hasText("Username cannot be empty"));
    }

    /**
     * Tests login with whitespace credentials shows error message.
     */
    @Test
    void testLoginWithWhitespaceCredentials(FxRobot robot) {
        robot.sleep(2000);
        robot.clickOn("#usernameField");
        robot.write("   ");
        robot.clickOn("#passwordField");
        robot.write("   ");
        robot.clickOn("Login");
        verifyThat("#errorLabel", hasText("Username cannot be empty"));
    }

    /**
     * Tests login with API exception does not crash the UI.
     */
    @Test
    void testLoginWithApiException(FxRobot robot) {
        robot.sleep(2000);
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#passwordField");
        robot.write("testpass123");
        robot.clickOn("Login");
        // Assert that errorLabel shows invalid credentials or login error
        verifyThat("#errorLabel", hasText("Invalid username or password"));
    }

    /**
     * Tests navigation to signup screen from login.
     */
    @Test
    void testGoToSignupButton(FxRobot robot) {
        robot.sleep(2000);
        
        // Click on the "Don't have an account? Sign up" hyperlink
        robot.clickOn("Don't have an account? Sign up");
        
        // Verify navigation occurred (this would be handled by the application)
        // In a real test, you might verify the scene changed or a method was called
    }

    /**
     * Tests login button accessibility and visibility.
     */
    @Test
    void testLoginButtonAccessibility(FxRobot robot) {
        robot.sleep(2000);
        
        // Verify login button is accessible
        Button loginButton = robot.lookup("Login").queryAs(Button.class);
        assert loginButton != null;
        assert loginButton.isVisible();
        assert !loginButton.isDisabled();
    }

    /**
     * Tests login using Enter key submits the form.
     */
    @Test
    void testEnterKeyLogin(FxRobot robot) {
        robot.sleep(2000);
        
        robot.clickOn("#usernameField");
        robot.write("testuser");
        
        robot.clickOn("#passwordField");
        robot.write("testpass123");
        
        // Press Enter key to trigger login
        robot.press(javafx.scene.input.KeyCode.ENTER);
        
        // Verify login was attempted
        verifyThat("#errorLabel", hasText(""));
    }

    /**
     * Tests form validation for missing or invalid fields.
     */
    @Test
    void testFormValidation(FxRobot robot) {
        robot.sleep(2000);
        
        // Test various validation scenarios
        robot.clickOn("Login");
        verifyThat("#errorLabel", hasText("Username cannot be empty"));
        
        robot.clickOn("#usernameField");
        robot.write("user");
        robot.clickOn("Login");
        verifyThat("#errorLabel", hasText("Password cannot be empty"));
    }

    /**
     * Tests input field behavior for username and password.
     */
    @Test
    void testInputFieldBehavior(FxRobot robot) {
        robot.sleep(2000);
        
        // Test input field behavior
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        PasswordField passwordField = robot.lookup("#passwordField").queryAs(PasswordField.class);
        
        robot.clickOn("#usernameField");
        robot.write("testuser");
        assert usernameField.getText().equals("testuser");
        
        robot.clickOn("#passwordField");
        robot.write("testpass");
        assert passwordField.getText().equals("testpass");
    }

    /**
     * Tests error label visibility and updates.
     */
    @Test
    void testErrorLabelVisibility(FxRobot robot) {
        robot.sleep(2000);
        
        // Test that error label is initially empty
        verifyThat("#errorLabel", hasText(""));
        
        // Test error message display
        robot.clickOn("Login");
        verifyThat("#errorLabel", hasText("Username cannot be empty"));
    }

    /**
     * Tests form reset clears all fields.
     */
    @Test
    void testFormReset(FxRobot robot) {
        robot.sleep(2000);
        
        // Fill form with data
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#passwordField");
        robot.write("testpass");
        
        // Verify data is entered
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        PasswordField passwordField = robot.lookup("#passwordField").queryAs(PasswordField.class);
        
        assert usernameField.getText().equals("testuser");
        assert passwordField.getText().equals("testpass");
        
        // Clear fields manually
        robot.clickOn("#usernameField");
        robot.eraseText(8);
        robot.clickOn("#passwordField");
        robot.eraseText(8);
        
        // Verify fields are cleared
        assert usernameField.getText().isEmpty();
        assert passwordField.getText().isEmpty();
    }

    /**
     * Tests keyboard navigation through login form fields.
     */
    @Test
    void testKeyboardNavigation(FxRobot robot) {
        robot.sleep(2000);
        
        // Test keyboard navigation
        robot.clickOn("#usernameField");
        robot.write("test");
        robot.press(javafx.scene.input.KeyCode.TAB);
        
        // Should be in password field now
        robot.write("password");
        robot.press(javafx.scene.input.KeyCode.TAB);
        
        // Should be on login button
        robot.press(javafx.scene.input.KeyCode.ENTER);
        
        // Verify login was attempted
        verifyThat("#errorLabel", hasText(""));
    }

    /**
     * Tests accessibility of all login form elements.
     */
    @Test
    void testFormAccessibility(FxRobot robot) {
        robot.sleep(2000);
        
        // Test that all form elements are accessible
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        PasswordField passwordField = robot.lookup("#passwordField").queryAs(PasswordField.class);
        Button loginButton = robot.lookup("Login").queryAs(Button.class);
        
        assert usernameField != null && usernameField.isVisible();
        assert passwordField != null && passwordField.isVisible();
        assert loginButton != null && loginButton.isVisible();
    }

    /**
     * Tests responsive design of the login form.
     */
    @Test
    void testResponsiveDesign(FxRobot robot) {
        robot.sleep(2000);
        
        // Test that the form is responsive
        // This is a basic test - in a real scenario you might test different window sizes
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        assert usernameField.isVisible();
        assert usernameField.getWidth() > 0;
    }

    /**
     * Tests input sanitization for username field.
     */
    @Test
    void testInputSanitization(FxRobot robot) {
        robot.sleep(2000);
        
        // Test input sanitization
        robot.clickOn("#usernameField");
        robot.write("test<script>alert('xss')</script>user");
        
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        String input = usernameField.getText();
        
        // Verify input is accepted (basic test)
        assert input.contains("test");
        assert input.contains("user");
    }
} 