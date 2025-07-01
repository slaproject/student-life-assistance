package com.studentapp.frontend.controller;

import com.studentapp.common.model.CalendarEvent;
import com.studentapp.frontend.client.CalendarApiClient;
import com.studentapp.frontend.view.CalendarView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

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
        if (calendarView != null) {
            calendarView.setJwtToken(token);
        }
    }

    private void setupListeners() {
        calendarView.setOnMonthChangeListener(this::loadEventsForMonth);
        calendarView.setOnAddEventListener(this::handleAddEvent);
        calendarView.setOnEditEventListener(this::handleEditEvent);
        calendarView.setOnDeleteEventListener(this::handleDeleteEvent);
    }

    private void loadEventsForMonth(YearMonth yearMonth) {
        new Thread(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
            allEvents = events;
            Map<LocalDate, List<CalendarEvent>> eventsByDate = events.stream()
                    .filter(e -> e.getStartTime() != null &&
                            YearMonth.from(e.getStartTime().toLocalDate()).equals(yearMonth))
                    .collect(Collectors.groupingBy(e -> e.getStartTime().toLocalDate()));
            Platform.runLater(() -> calendarView.updateCalendar(yearMonth, eventsByDate));
        }).start();
    }

    private void handleAddEvent(LocalDate date) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Event");
        dialog.setHeaderText("Add Event on " + date);
        dialog.setContentText("Event Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            CalendarEvent event = new CalendarEvent();
            event.setEventName(name);
            event.setStartTime(date.atTime(10, 0));
            event.setEndTime(date.atTime(11, 0));
            event.setEventType(CalendarEvent.EventType.OTHER);
            apiClient.addEvent(event);
            loadEventsForMonth(calendarView.getCurrentYearMonth());
        });
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
        apiClient.deleteEvent(event.getId());
        loadEventsForMonth(calendarView.getCurrentYearMonth());
    }
}
