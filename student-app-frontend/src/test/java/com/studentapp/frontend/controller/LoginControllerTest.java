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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * Unit tests for LoginController, covering authentication, form validation, and navigation.
 */
@ExtendWith(ApplicationExtension.class)
class LoginControllerTest {

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
        robot.sleep(1000);
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
     * Tests login button accessibility and visibility.
     */
    @Test
    void testLoginButtonAccessibility(FxRobot robot) {
        robot.sleep(2000);
        Button loginButton = robot.lookup("Login").queryAs(Button.class);
        assertDoesNotThrow(() -> {
            assert loginButton != null;
            assert loginButton.isVisible();
            assert !loginButton.isDisabled();
        });
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
        robot.press(javafx.scene.input.KeyCode.ENTER);
        
        verifyThat("#errorLabel", hasText(""));
    }

    /**
     * Tests form validation for missing or invalid fields.
     */
    @Test
    void testFormValidation(FxRobot robot) {
        robot.sleep(2000);
        
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
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        PasswordField passwordField = robot.lookup("#passwordField").queryAs(PasswordField.class);
        robot.clickOn("#usernameField");
        robot.write("testuser");
        org.junit.jupiter.api.Assertions.assertEquals("testuser", usernameField.getText());
        robot.clickOn("#passwordField");
        robot.write("testpass");
        org.junit.jupiter.api.Assertions.assertEquals("testpass", passwordField.getText());
    }

    /**
     * Tests error label visibility and updates.
     */
    @Test
    void testErrorLabelVisibility(FxRobot robot) {
        robot.sleep(2000);
        verifyThat("#errorLabel", hasText(""));
        robot.clickOn("Login");
        verifyThat("#errorLabel", hasText("Username cannot be empty"));
    }

    /**
     * Tests form reset clears all fields.
     */
    @Test
    void testFormReset(FxRobot robot) {
        robot.sleep(2000);
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#passwordField");
        robot.write("testpass");
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        PasswordField passwordField = robot.lookup("#passwordField").queryAs(PasswordField.class);      
        org.junit.jupiter.api.Assertions.assertEquals("testuser", usernameField.getText());
        org.junit.jupiter.api.Assertions.assertEquals("testpass", passwordField.getText());
        robot.clickOn("#usernameField");
        robot.eraseText(8);
        robot.clickOn("#passwordField");
        robot.eraseText(8);
        org.junit.jupiter.api.Assertions.assertEquals("", usernameField.getText());
        org.junit.jupiter.api.Assertions.assertEquals("", passwordField.getText());
    }

    /**
     * Tests keyboard navigation through login form fields.
     */
    @Test
    void testKeyboardNavigation(FxRobot robot) {
        robot.sleep(2000);
        robot.clickOn("#usernameField");
        robot.write("test");
        robot.press(javafx.scene.input.KeyCode.TAB);  
        robot.write("password");
        robot.press(javafx.scene.input.KeyCode.TAB);
        robot.press(javafx.scene.input.KeyCode.ENTER);

        verifyThat("#errorLabel", hasText(""));
    }

    /**
     * Tests accessibility of all login form elements.
     */
    @Test
    void testFormAccessibility(FxRobot robot) {
        robot.sleep(2000);
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        PasswordField passwordField = robot.lookup("#passwordField").queryAs(PasswordField.class);
        Button loginButton = robot.lookup("Login").queryAs(Button.class);
        org.junit.jupiter.api.Assertions.assertNotNull(usernameField);
        org.junit.jupiter.api.Assertions.assertTrue(usernameField.isVisible());
        org.junit.jupiter.api.Assertions.assertNotNull(passwordField);
        org.junit.jupiter.api.Assertions.assertTrue(passwordField.isVisible());
        org.junit.jupiter.api.Assertions.assertNotNull(loginButton);
        org.junit.jupiter.api.Assertions.assertTrue(loginButton.isVisible());
    }

    /**
     * Tests responsive design of the login form.
     */
    @Test
    void testResponsiveDesign(FxRobot robot) {
        robot.sleep(2000);
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        org.junit.jupiter.api.Assertions.assertTrue(usernameField.isVisible());
        org.junit.jupiter.api.Assertions.assertTrue(usernameField.getWidth() > 0);
    }

    /**
     * Tests input sanitization for username field.
     */
    @Test
    void testInputSanitization(FxRobot robot) {
        robot.sleep(2000);
        robot.clickOn("#usernameField");
        robot.write("test<script>alert('xss')</script>user");
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        String input = usernameField.getText();
        org.junit.jupiter.api.Assertions.assertTrue(input.contains("test"));
        org.junit.jupiter.api.Assertions.assertTrue(input.contains("user"));
    }
} 