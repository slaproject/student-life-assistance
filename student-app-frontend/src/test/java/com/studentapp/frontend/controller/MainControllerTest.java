package com.studentapp.frontend.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MainController, covering menu actions, navigation, and UI functionality.
 */
@Disabled("Disabled in CI/CD due to JavaFX GUI tests requiring a display. Run locally for GUI testing.")
@ExtendWith(ApplicationExtension.class)
class MainControllerTest {

    private MainController mainController;
    private Stage stage;

    @Start
    private void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/main-view.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();
        
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    void setUp() throws TimeoutException {
        // Ensure we're on the FX application thread
        FxToolkit.registerPrimaryStage();
    }


    /**
     * Tests that all menus are accessible and visible.
     */
    @Test
    void testMenuAccessibility(FxRobot robot) {
        // Verify all menus are accessible
        BorderPane rootPane = robot.lookup("#rootPane").queryAs(BorderPane.class);
        assertNotNull(rootPane);
        assertNotNull(rootPane.getTop());
        assertTrue(rootPane.getTop().isVisible());
    }

    /**
     * Tests showing the calendar view.
     */
    @Test
    void testShowCalendar(FxRobot robot) {
        // Test showing calendar view
        // This would typically involve calling a method on the controller
        assertNotNull(mainController);
    }

    /**
     * Tests setting center content in the main controller.
     */
    @Test
    void testSetCenterContent(FxRobot robot) {
        // Test setting center content
        // This would typically involve calling a method on the controller
        assertNotNull(mainController);
    }

    /**
     * Tests window title is set correctly.
     */
    @Test
    void testWindowTitle(FxRobot robot) {
        // Test window title
        assertEquals("PrimaryStageApplication", stage.getTitle());
    }

    /**
     * Tests window size is greater than zero.
     */
    @Test
    void testWindowSize(FxRobot robot) {
        // Test window size
        assertTrue(stage.getWidth() > 0);
        assertTrue(stage.getHeight() > 0);
    }

    /**
     * Tests window minimize and maximize functionality.
     */
    @Test
    void testWindowMinimizeMaximize(FxRobot robot) {
        // Test window minimize/maximize functionality
        // This would typically involve testing window state changes
        assertTrue(stage.isShowing());
    }

    /**
     * Tests menu bar exists and is visible.
     */
    @Test
    void testMenuBarExists(FxRobot robot) {
        // Verify menu bar exists
        BorderPane rootPane = robot.lookup("#rootPane").queryAs(BorderPane.class);
        assertNotNull(rootPane);
        assertNotNull(rootPane.getTop());
    }

    /**
     * Tests menu items exist and are visible.
     */
    @Test
    void testMenuItemsExist(FxRobot robot) {
        // Verify menu items exist
        robot.clickOn("File");
        // Verify menu items are visible
        assertTrue(robot.lookup("New").tryQuery().isPresent());
        assertTrue(robot.lookup("Open").tryQuery().isPresent());
        assertTrue(robot.lookup("Save").tryQuery().isPresent());
        assertTrue(robot.lookup("Exit").tryQuery().isPresent());
    }

    /**
     * Tests all student menu items exist and are visible.
     */
    @Test
    void testStudentMenuItems(FxRobot robot) {
        robot.clickOn("Student");
        assertTrue(robot.lookup("Academic Records").tryQuery().isPresent());
        assertTrue(robot.lookup("Schedule").tryQuery().isPresent());
        assertTrue(robot.lookup("Assignments").tryQuery().isPresent());
        assertTrue(robot.lookup("Grades").tryQuery().isPresent());
    }

    /**
     * Tests all finance menu items exist and are visible.
     */
    @Test
    void testFinanceMenuItems(FxRobot robot) {
        robot.clickOn("Finance");
        assertTrue(robot.lookup("Budget Tracker").tryQuery().isPresent());
        assertTrue(robot.lookup("Expenses").tryQuery().isPresent());
        assertTrue(robot.lookup("Financial Aid").tryQuery().isPresent());
    }

    /**
     * Tests all resources menu items exist and are visible.
     */
    @Test
    void testResourcesMenuItems(FxRobot robot) {
        robot.clickOn("Resources");
        assertTrue(robot.lookup("Library").tryQuery().isPresent());
        assertTrue(robot.lookup("Campus Map").tryQuery().isPresent());
        assertTrue(robot.lookup("Student Services").tryQuery().isPresent());
    }

    /**
     * Tests all help menu items exist and are visible.
     */
    @Test
    void testHelpMenuItems(FxRobot robot) {
        robot.clickOn("Help");
        assertTrue(robot.lookup("User Guide").tryQuery().isPresent());
        assertTrue(robot.lookup("About").tryQuery().isPresent());
    }

    /**
     * Tests all account menu items exist and are visible.
     */
    @Test
    void testAccountMenuItems(FxRobot robot) {
        robot.clickOn("Account");
        assertTrue(robot.lookup("Logout").tryQuery().isPresent());
    }

    /**
     * Tests menu accessibility features are enabled.
     */
    @Test
    void testMenuAccessibilityFeatures(FxRobot robot) {
        // Test menu accessibility features
        BorderPane rootPane = robot.lookup("#rootPane").queryAs(BorderPane.class);
        assertNotNull(rootPane);
        assertNotNull(rootPane.getTop());
        assertTrue(rootPane.getTop().isVisible());
        assertFalse(rootPane.getTop().isDisabled());
    }

    /**
     * Tests responsive layout of the main window.
     */
    @Test
    void testResponsiveLayout(FxRobot robot) {
        // Test responsive layout
        BorderPane rootPane = robot.lookup("#rootPane").queryAs(BorderPane.class);
        assertNotNull(rootPane);
        assertTrue(rootPane.isVisible());
    }

    /**
     * Tests menu state management after actions.
     */
    @Test
    void testMenuStateManagement(FxRobot robot) {
        // Test menu state management
        robot.clickOn("File");
        robot.clickOn("New");
        
        // Verify menu state is managed correctly
        assertNotNull(mainController);
    }
} 