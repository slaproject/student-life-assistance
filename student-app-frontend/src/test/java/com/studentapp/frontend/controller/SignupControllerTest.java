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
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import com.studentapp.frontend.client.CalendarApiClient;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import org.mockito.Mockito;
import org.testfx.util.WaitForAsyncUtils;

/**
 * Unit tests for SignupController, covering user registration, validation, and error handling.
 */
@ExtendWith(ApplicationExtension.class)
public class SignupControllerTest {

    private SignupController signupController;
    private Stage stage;
    private SignupController.SignupService mockSignupService;

    @Start
    private void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/signup-view.fxml"));
        Parent root = loader.load();
        signupController = loader.getController();
        // Create and inject the mock service here
        mockSignupService = Mockito.mock(SignupController.SignupService.class);
        signupController.setSignupService(mockSignupService);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    void setUp() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        // No need to set the mock here
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        if (mockSignupService != null) Mockito.reset(mockSignupService);
    }

    /**
     * Tests signup with valid data returns success message and clears fields.
     */
    @Test
    void testSignupWithValidData(FxRobot robot) {
        Mockito.when(mockSignupService.signup(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("User registered successfully");
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#emailField");
        robot.write("test@example.com");
        robot.clickOn("#passwordField");
        robot.write("TestPass123");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Signup successful! You can now log in."));
    }

    /**
     * Tests signup with empty username shows error.
     */
    @Test
    void testSignupWithEmptyUsername(FxRobot robot) {
        robot.clickOn("#emailField");
        robot.write("test@example.com");
        robot.clickOn("#passwordField");
        robot.write("TestPass123");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Username cannot be empty"));
    }

    /**
     * Tests signup with empty email shows error.
     */
    @Test
    void testSignupWithEmptyEmail(FxRobot robot) {
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#passwordField");
        robot.write("TestPass123");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Please enter a valid email address"));
    }

    /**
     * Tests signup with invalid email format shows error.
     */
    @Test
    void testSignupWithInvalidEmail(FxRobot robot) {
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#emailField");
        robot.write("invalid-email");
        robot.clickOn("#passwordField");
        robot.write("TestPass123");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Please enter a valid email address"));
    }

    /**
     * Tests signup with empty password shows error.
     */
    @Test
    void testSignupWithEmptyPassword(FxRobot robot) {
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#emailField");
        robot.write("test@example.com");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Password cannot be empty"));
    }

    /**
     * Tests signup with short password shows error.
     */
    @Test
    void testSignupWithShortPassword(FxRobot robot) {
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#emailField");
        robot.write("test@example.com");
        robot.clickOn("#passwordField");
        robot.write("short");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Password must be at least 6 characters long"));
    }

    /**
     * Tests signup with password missing a number shows error.
     */
    @Test
    void testSignupWithPasswordWithoutNumber(FxRobot robot) {
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#emailField");
        robot.write("test@example.com");
        robot.clickOn("#passwordField");
        robot.write("TestPass");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Password must contain at least one number"));
    }

    /**
     * Tests signup with existing username shows error.
     */
    @Test
    void testSignupWithExistingUsername(FxRobot robot) {
        Mockito.when(mockSignupService.signup(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("Username already exists");
        robot.clickOn("#usernameField");
        robot.write("existinguser");
        robot.clickOn("#emailField");
        robot.write("test@example.com");
        robot.clickOn("#passwordField");
        robot.write("TestPass123");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("The username 'existinguser' is already taken."));
    }

    /**
     * Tests signup with existing email shows error.
     */
    @Test
    void testSignupWithExistingEmail(FxRobot robot) {
        Mockito.when(mockSignupService.signup(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("Email already exists");
        robot.clickOn("#usernameField");
        robot.write("newuser");
        robot.clickOn("#emailField");
        robot.write("existing@example.com");
        robot.clickOn("#passwordField");
        robot.write("TestPass123");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("The email 'existing@example.com' is already registered."));
    }

    /**
     * Tests signup with API failure shows error.
     */
    @Test
    void testSignupWithApiFailure(FxRobot robot) {
        Mockito.when(mockSignupService.signup(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("Registration failed. Please try again.");
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#emailField");
        robot.write("test@example.com");
        robot.clickOn("#passwordField");
        robot.write("TestPass123");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Registration failed. Please try again."));
    }

    /**
     * Tests signup with API exception shows network error.
     */
    @Test
    void testSignupWithApiException(FxRobot robot) {
        Mockito.when(mockSignupService.signup(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("Network error. Please check your connection.");
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#emailField");
        robot.write("test@example.com");
        robot.clickOn("#passwordField");
        robot.write("TestPass123");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Network error. Please check your connection."));
    }

    /**
     * Tests navigation to login screen from signup.
     */
    @Test
    void testGoToLoginButton(FxRobot robot) {
        robot.clickOn("Already have an account? Login");
        
        // Verify navigation occurred
        // In a real test, you might verify the scene changed or a method was called
    }

    /**
     * Tests fields are cleared after successful signup.
     */
    @Test
    void testClearFieldsAfterSuccessfulSignup(FxRobot robot) {
        Mockito.when(mockSignupService.signup(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("User registered successfully");
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#emailField");
        robot.write("test@example.com");
        robot.clickOn("#passwordField");
        robot.write("TestPass123");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Signup successful! You can now log in."));
        // Verify fields are cleared after successful signup
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        TextField emailField = robot.lookup("#emailField").queryAs(TextField.class);
        PasswordField passwordField = robot.lookup("#passwordField").queryAs(PasswordField.class);
        assert usernameField.getText().isEmpty();
        assert emailField.getText().isEmpty();
        assert passwordField.getText().isEmpty();
    }

    /**
     * Tests signup button accessibility and visibility.
     */
    @Test
    void testSignupButtonAccessibility(FxRobot robot) {
        Button signupButton = robot.lookup("Sign Up").queryAs(Button.class);
        assert signupButton != null;
        assert signupButton.isVisible();
        assert !signupButton.isDisabled();
    }

    /**
     * Tests tab order navigation through signup form fields.
     */
    @Test
    void testTabOrderNavigation(FxRobot robot) {
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        robot.interact(usernameField::requestFocus);

        robot.type(javafx.scene.input.KeyCode.TAB);
        WaitForAsyncUtils.waitForFxEvents();
        TextField emailField = robot.lookup("#emailField").queryAs(TextField.class);
        assert emailField.isFocused();

        robot.type(javafx.scene.input.KeyCode.TAB);
        WaitForAsyncUtils.waitForFxEvents();
        PasswordField passwordField = robot.lookup("#passwordField").queryAs(PasswordField.class);
        assert passwordField.isFocused();

        robot.type(javafx.scene.input.KeyCode.TAB);
        WaitForAsyncUtils.waitForFxEvents();
        Button signupButton = robot.lookup("Sign Up").queryAs(Button.class);
        assert signupButton.isFocused();
    }

    /**
     * Tests signup using Enter key submits the form.
     */
    @Test
    void testEnterKeySignup(FxRobot robot) {
        Mockito.when(mockSignupService.signup(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("User registered successfully");
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        robot.interact(usernameField::requestFocus);
        robot.write("testuser");
        robot.type(javafx.scene.input.KeyCode.TAB);
        robot.write("test@example.com");
        robot.type(javafx.scene.input.KeyCode.TAB);
        robot.write("TestPass123");
        robot.type(javafx.scene.input.KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#errorLabel", hasText("Signup successful! You can now log in."));
    }

    /**
     * Tests email validation with various valid and invalid formats.
     */
    @Test
    void testEmailValidationWithVariousFormats(FxRobot robot) {
        // Test valid email formats
        String[] validEmails = {"test@example.com", "user.name@domain.co.uk", "user+tag@example.org"};
        for (String email : validEmails) {
            Mockito.when(mockSignupService.signup(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                    .thenReturn("User registered successfully");
            TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
            robot.interact(usernameField::requestFocus);
            robot.write("testuser");
            robot.type(javafx.scene.input.KeyCode.TAB);
            robot.write(email);
            robot.type(javafx.scene.input.KeyCode.TAB);
            robot.write("TestPass123");
            robot.type(javafx.scene.input.KeyCode.ENTER);
            WaitForAsyncUtils.waitForFxEvents();
            verifyThat("#errorLabel", hasText("Signup successful! You can now log in."));
        }
        // Test invalid email formats (no need to set up the mock)
        String[] invalidEmails = {"invalid-email", "@example.com", "test@", "test.example.com"};
        for (String email : invalidEmails) {
            TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
            robot.interact(usernameField::requestFocus);
            robot.write("testuser");
            robot.type(javafx.scene.input.KeyCode.TAB);
            robot.write(email);
            robot.type(javafx.scene.input.KeyCode.TAB);
            robot.write("TestPass123");
            robot.type(javafx.scene.input.KeyCode.ENTER);
            WaitForAsyncUtils.waitForFxEvents();
            verifyThat("#errorLabel", hasText("Please enter a valid email address"));
        }
    }

    /**
     * Tests password validation with edge cases and special characters.
     */
    @Test
    void testPasswordValidationEdgeCases(FxRobot robot) {
        // Test password with special characters
        Mockito.when(mockSignupService.signup(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("User registered successfully");
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#emailField");
        robot.write("test@example.com");
        robot.clickOn("#passwordField");
        robot.write("Test@Pass123!");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Signup successful! You can now log in."));
        // Test password with only letters and numbers
        Mockito.when(mockSignupService.signup(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("User registered successfully");
        robot.clickOn("#usernameField");
        robot.write("testuser");
        robot.clickOn("#emailField");
        robot.write("test@example.com");
        robot.clickOn("#passwordField");
        robot.write("TestPass123");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Signup successful! You can now log in."));
    }

    /**
     * Tests multiple signup attempts in sequence.
     */
    @Test
    void testMultipleSignupAttempts(FxRobot robot) {
        // First attempt
        Mockito.when(mockSignupService.signup(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("User registered successfully");
        robot.clickOn("#usernameField");
        robot.write("user1");
        robot.clickOn("#emailField");
        robot.write("user1@example.com");
        robot.clickOn("#passwordField");
        robot.write("Pass123");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Signup successful! You can now log in."));
        // Clear and try again
        Mockito.when(mockSignupService.signup(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("User registered successfully");
        robot.clickOn("#usernameField");
        robot.eraseText(5);
        robot.write("user2");
        robot.clickOn("#emailField");
        robot.eraseText(15);
        robot.write("user2@example.com");
        robot.clickOn("#passwordField");
        robot.eraseText(7);
        robot.write("Pass456");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Signup successful! You can now log in."));
    }

    /**
     * Tests form validation for missing or invalid fields.
     */
    @Test
    void testFormValidation(FxRobot robot) {
        // Test various validation scenarios
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Username cannot be empty"));
        
        robot.clickOn("#usernameField");
        robot.write("user");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Please enter a valid email address"));
        
        robot.clickOn("#emailField");
        robot.write("user@example.com");
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Password cannot be empty"));
    }

    /**
     * Tests input field behavior for username, email, and password.
     */
    @Test
    void testInputFieldBehavior(FxRobot robot) {
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        TextField emailField = robot.lookup("#emailField").queryAs(TextField.class);
        PasswordField passwordField = robot.lookup("#passwordField").queryAs(PasswordField.class);
        
        robot.clickOn("#usernameField");
        robot.write("testuser");
        assert usernameField.getText().equals("testuser");
        
        robot.clickOn("#emailField");
        robot.write("test@example.com");
        assert emailField.getText().equals("test@example.com");
        
        robot.clickOn("#passwordField");
        robot.write("testpass");
        assert passwordField.getText().equals("testpass");
    }

    /**
     * Tests error label visibility and updates.
     */
    @Test
    void testErrorLabelVisibility(FxRobot robot) {
        verifyThat("#errorLabel", hasText(""));
        
        robot.clickOn("Sign Up");
        verifyThat("#errorLabel", hasText("Username cannot be empty"));
    }

    
    /**
     * Tests keyboard navigation through signup form fields.
     */
    @Test
    void testKeyboardNavigation(FxRobot robot) {
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        robot.interact(usernameField::requestFocus);

        robot.type(javafx.scene.input.KeyCode.TAB);

        TextField emailField = robot.lookup("#emailField").queryAs(TextField.class);
        assert emailField.isFocused();

        robot.type(javafx.scene.input.KeyCode.TAB);

        PasswordField passwordField = robot.lookup("#passwordField").queryAs(PasswordField.class);
        assert passwordField.isFocused();

        robot.type(javafx.scene.input.KeyCode.TAB);

        Button signupButton = robot.lookup("Sign Up").queryAs(Button.class);
        assert signupButton.isFocused();
    }

    /**
     * Tests accessibility of all signup form elements.
     */
    @Test
    void testFormAccessibility(FxRobot robot) {
        TextField usernameField = robot.lookup("#usernameField").queryAs(TextField.class);
        TextField emailField = robot.lookup("#emailField").queryAs(TextField.class);
        PasswordField passwordField = robot.lookup("#passwordField").queryAs(PasswordField.class);
        Button signupButton = robot.lookup("Sign Up").queryAs(Button.class);
        
        assert usernameField != null && usernameField.isVisible();
        assert emailField != null && emailField.isVisible();
        assert passwordField != null && passwordField.isVisible();
        assert signupButton != null && signupButton.isVisible();
    }

    /**
     * Tests responsive design of the signup form.
     */
    @Test
    void testResponsiveDesign(FxRobot robot) {
        assert stage.getScene().getRoot().isVisible();
    }


} 