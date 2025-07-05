package com.studentapp.frontend.controller;

import com.studentapp.common.model.CalendarEvent;
import com.studentapp.frontend.client.CalendarApiClient;
import com.studentapp.frontend.view.CalendarView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class CalendarController implements Initializable {
    @FXML
    private VBox rootVBox;
    private CalendarView calendarView;
    private String jwtToken;
    private final CalendarApiClient apiClient;
    private List<CalendarEvent> allEvents = new ArrayList<>();

    public CalendarController() {
        this.apiClient = new CalendarApiClient();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        calendarView = new CalendarView();
        rootVBox.getChildren().add(calendarView);
        setupListeners();
        loadEventsForMonth(YearMonth.now());
    }

    public void setJwtToken(String token) {
        this.jwtToken = token;
        if(jwtToken != null) {
            calendarView.setJwtToken(jwtToken);
        }
    }

    public void clearJwtToken() {
        this.jwtToken = null;
        apiClient.clearJwtToken();
    }

    private void setupListeners() {
        calendarView.setOnMonthChangeListener(this::loadEventsForMonth);
        calendarView.setOnAddEventListener(this::handleAddEvent);
        calendarView.setOnEditEventListener(this::handleEditEvent);
        calendarView.setOnDeleteEventListener(this::handleDeleteEvent);
    }

    private void loadEventsForMonth(YearMonth yearMonth) {
        new Thread(() -> {
            apiClient.setJwtToken(jwtToken); // Ensure JWT token is set
            List<CalendarEvent> events = apiClient.getEvents();
            System.out.println("Events retrieved: " + events.size());
            allEvents = events;
            
            // Remove the yearMonth filter to show all events
            Map<LocalDate, List<CalendarEvent>> eventsByDate = events.stream()
                    .filter(e -> e.getStartTime() != null)
                    .collect(Collectors.groupingBy(e -> e.getStartTime().toLocalDate()));
            
            System.out.println("Events by date: " + eventsByDate);
            Platform.runLater(() -> calendarView.updateCalendar(yearMonth, eventsByDate));
        }).start();
    }

    private void handleAddEvent(LocalDate date) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/add-event-view.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Event");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));

            TextField eventNameField = (TextField) root.lookup("#eventNameField");
            TextArea descriptionField = (TextArea) root.lookup("#descriptionField");
            TextField startTimeField = (TextField) root.lookup("#startTimeField");
            TextField endTimeField = (TextField) root.lookup("#endTimeField");
            TextField meetingLinksField = (TextField) root.lookup("#meetingLinksField");
            ComboBox<String> eventTypeCombo = (ComboBox<String>) root.lookup("#eventTypeCombo");
            Button submitButton = (Button) root.lookup("#submitButton");
            Button cancelButton = (Button) root.lookup("#cancelButton");

            // Set ComboBox items in Java
            eventTypeCombo.getItems().setAll("MEETING", "PERSONAL", "FINANCIAL", "APPOINTMENT", "OTHER");
            eventTypeCombo.getSelectionModel().select("OTHER");

            // Set default values
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            startTimeField.setText(date.atTime(10, 0).format(dtf));
            endTimeField.setText(date.atTime(11, 0).format(dtf));

            submitButton.setOnAction(e -> {
                try {
                    String name = eventNameField.getText();
                    String description = descriptionField.getText();
                    String startTimeStr = startTimeField.getText();
                    String endTimeStr = endTimeField.getText();
                    String meetingLinks = meetingLinksField.getText();
                    String eventTypeStr = eventTypeCombo.getValue();

                    // Basic validation
                    if (name == null || name.trim().isEmpty()) {
                        showAlert("Event name is required.");
                        return;
                    }
                    if (startTimeStr == null || endTimeStr == null || startTimeStr.trim().isEmpty() || endTimeStr.trim().isEmpty()) {
                        showAlert("Start and end time are required.");
                        return;
                    }
                    LocalDateTime startTime, endTime;
                    try {
                        startTime = LocalDateTime.parse(startTimeStr, dtf);
                    } catch (Exception ex) {
                        showAlert("Invalid start time format. Use yyyy-MM-dd HH:mm.");
                        return;
                    }
                    try {
                        endTime = LocalDateTime.parse(endTimeStr, dtf);
                    } catch (Exception ex) {
                        showAlert("Invalid end time format. Use yyyy-MM-dd HH:mm.");
                        return;
                    }
                    if (!endTime.isAfter(startTime)) {
                        showAlert("End time must be after start time.");
                        return;
                    }
                    if (eventTypeStr == null || eventTypeStr.trim().isEmpty()) {                      showAlert("Event type is required.");
                        return;
                    }

                    CalendarEvent.EventType eventType = CalendarEvent.EventType.valueOf(eventTypeStr);

                    CalendarEvent event = new CalendarEvent();
                    event.setEventName(name);
                    event.setDescription(description);
                    event.setStartTime(startTime);
                    event.setEndTime(endTime);
                    event.setMeetingLinks(meetingLinks);
                    event.setEventType(eventType);
                    // userId is set in backend or via token
                    System.out.println("Add event: "+jwtToken);
                    apiClient.setJwtToken(jwtToken);
                    apiClient.addEvent(event);
                    loadEventsForMonth(calendarView.getCurrentYearMonth());
                    dialogStage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            cancelButton.setOnAction(e -> dialogStage.close());

            dialogStage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleEditEvent(CalendarEvent event) {
        TextInputDialog dialog = new TextInputDialog(event.getEventName());
        dialog.setTitle("Edit Event");
        dialog.setHeaderText("Edit Event on " + event.getStartTime().toLocalDate());
        dialog.setContentText("Event Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            event.setEventName(name);
            apiClient.updateEvent(event);
            loadEventsForMonth(calendarView.getCurrentYearMonth());
        });
    }

    private void handleDeleteEvent(CalendarEvent event) {
        apiClient.setJwtToken(jwtToken);
        apiClient.deleteEvent(event.getId());
        loadEventsForMonth(calendarView.getCurrentYearMonth());
    }


    // Helper for showing alerts
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
