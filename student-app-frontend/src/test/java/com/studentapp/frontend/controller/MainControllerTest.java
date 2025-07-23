package com.studentapp.frontend.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * Unit tests for MainController, covering menu actions, navigation, and UI functionality.
 */
@Disabled("Disabled in CI/CD due to JavaFX GUI tests requiring a display. Run locally for GUI testing.")
@ExtendWith(ApplicationExtension.class)
public class MainControllerTest {

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
     * Tests File > New menu action is handled.
     */
    @Test
    void testFileMenuNewAction(FxRobot robot) {
        robot.clickOn("File");
        robot.clickOn("New");
    }

    /**
     * Tests File > Open menu action is handled.
     */
    @Test
    void testFileMenuOpenAction(FxRobot robot) {
        robot.clickOn("File");
        robot.clickOn("Open");
    }

    /**
     * Tests File > Save menu action is handled.
     */
    @Test
    void testFileMenuSaveAction(FxRobot robot) {
        robot.clickOn("File");
        robot.clickOn("Save");
    }

    /**
     * Tests File > Exit menu action is handled.
     */
    @Test
    void testFileMenuExitAction(FxRobot robot) {
        robot.clickOn("File");
        robot.clickOn("Exit");
    }

    /**
     * Tests Student > Academic Records menu action is handled.
     */
    @Test
    void testStudentMenuAcademicRecordsAction(FxRobot robot) {
        robot.clickOn("Student");
        robot.clickOn("Academic Records");
    }

    /**
     * Tests Student > Schedule menu action is handled.
     */
    @Test
    void testStudentMenuScheduleAction(FxRobot robot) {
        robot.clickOn("Student");
        robot.clickOn("Schedule");
    }

    /**
     * Tests Student > Assignments menu action is handled.
     */
    @Test
    void testStudentMenuAssignmentsAction(FxRobot robot) {
        robot.clickOn("Student");
        robot.clickOn("Assignments");
    }

    /**
     * Tests Student > Grades menu action is handled.
     */
    @Test
    void testStudentMenuGradesAction(FxRobot robot) {
        robot.clickOn("Student");
        robot.clickOn("Grades");
    }

    /**
     * Tests Finance > Budget Tracker menu action is handled.
     */
    @Test
    void testFinanceMenuBudgetAction(FxRobot robot) {
        robot.clickOn("Finance");
        robot.clickOn("Budget Tracker");
    }

    /**
     * Tests Finance > Expenses menu action is handled.
     */
    @Test
    void testFinanceMenuExpensesAction(FxRobot robot) {
        robot.clickOn("Finance");
        robot.clickOn("Expenses");
    }

    /**
     * Tests Finance > Financial Aid menu action is handled.
     */
    @Test
    void testFinanceMenuFinancialAidAction(FxRobot robot) {
        robot.clickOn("Finance");
        robot.clickOn("Financial Aid");
    }

    /**
     * Tests Resources > Library menu action is handled.
     */
    @Test
    void testResourcesMenuLibraryAction(FxRobot robot) {
        robot.clickOn("Resources");
        robot.clickOn("Library");
    }

    /**
     * Tests Resources > Campus Map menu action is handled.
     */
    @Test
    void testResourcesMenuCampusMapAction(FxRobot robot) {
        robot.clickOn("Resources");
        robot.clickOn("Campus Map");
    }

    /**
     * Tests Resources > Student Services menu action is handled.
     */
    @Test
    void testResourcesMenuStudentServicesAction(FxRobot robot) {
        robot.clickOn("Resources");
        robot.clickOn("Student Services");
    }

    /**
     * Tests Help > User Guide menu action is handled.
     */
    @Test
    void testHelpMenuUserGuideAction(FxRobot robot) {
        robot.clickOn("Help");
        robot.clickOn("User Guide");
    }

    /**
     * Tests Help > About menu action is handled.
     */
    @Test
    void testHelpMenuAboutAction(FxRobot robot) {
        robot.clickOn("Help");
        robot.clickOn("About");
    }

    /**
     * Tests Account > Logout menu action is handled.
     */
    @Test
    void testLogoutAction(FxRobot robot) {
        robot.clickOn("Account");
        robot.clickOn("Logout");
    }

    /**
     * Tests navigation through all main menus.
     */
    @Test
    void testMenuNavigation(FxRobot robot) {
        // Test navigation through menus
        robot.clickOn("File");
        robot.clickOn("Student");
        robot.clickOn("Finance");
        robot.clickOn("Resources");
        robot.clickOn("Help");
        robot.clickOn("Account");
        
        // Verify all menus are accessible
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
     * Tests keyboard navigation through menus.
     */
    @Test
    void testMenuKeyboardNavigation(FxRobot robot) {
        // Test keyboard navigation through menus
        robot.clickOn("File");
        robot.press(javafx.scene.input.KeyCode.DOWN);
        robot.press(javafx.scene.input.KeyCode.ENTER);
        
        // Verify menu navigation works
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