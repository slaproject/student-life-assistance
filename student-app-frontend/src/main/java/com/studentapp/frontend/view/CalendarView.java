package com.studentapp.frontend.view;

import com.studentapp.common.model.CalendarEvent; // Use the common model
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A passive Calendar view component that only displays data.
 * It is managed by a controller and has no knowledge of services or APIs.
 */
public class CalendarView extends VBox {

  private YearMonth currentYearMonth;
  private final GridPane calendarGrid;
  private final Label monthYearLabel;
  private Consumer<YearMonth> onMonthChange;
  private Consumer<LocalDate> onDayClick;
  private Consumer<LocalDate> onAddEvent;
  private Consumer<CalendarEvent> onEditEvent;
  private Consumer<CalendarEvent> onDeleteEvent;
  private Button addEventButton;
  private LocalDate selectedDate;
  private String jwtToken;

  public CalendarView() {
    this.currentYearMonth = YearMonth.now();
    this.selectedDate = LocalDate.now(); // Default to today

    // Initialize components
    monthYearLabel = new Label();
    monthYearLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
    monthYearLabel.setAlignment(Pos.CENTER);

    Button previousMonthButton = new Button("←");
    previousMonthButton.setOnAction(e -> {
      currentYearMonth = currentYearMonth.minusMonths(1);
      if (onMonthChange != null) {
        onMonthChange.accept(currentYearMonth);
      }
    });

    Button nextMonthButton = new Button("→");
    nextMonthButton.setOnAction(e -> {
      currentYearMonth = currentYearMonth.plusMonths(1);
      if (onMonthChange != null) {
        onMonthChange.accept(currentYearMonth);
      }
    });

    calendarGrid = new GridPane();
    calendarGrid.setHgap(2);
    calendarGrid.setVgap(2);
    calendarGrid.setAlignment(Pos.CENTER);
    VBox.setVgrow(calendarGrid, Priority.ALWAYS);
    calendarGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    calendarGrid.setMinHeight(400);
    calendarGrid.setMinWidth(600);
    calendarGrid.setStyle("-fx-background-color: #f8f8f8;");

    HBox navigationBar = new HBox(10, previousMonthButton, monthYearLabel, nextMonthButton);
    navigationBar.setAlignment(Pos.CENTER);

    addEventButton = new Button("Add Event");
    addEventButton.setOnAction(e -> {
      System.out.println("[DEBUG] Add Event button clicked. selectedDate=" + selectedDate + ", onAddEvent=" + (onAddEvent != null));
      if (onAddEvent != null && selectedDate != null) {
        onAddEvent.accept(selectedDate);
      } else {
        System.out.println("[DEBUG] No date selected for Add Event or onAddEvent is null.");
      }
    });

    this.getChildren().addAll(navigationBar, calendarGrid, addEventButton);
    this.setFillWidth(true);
    this.setPrefHeight(600);
    this.setPrefWidth(900);
    this.setPadding(new Insets(10));
    this.setSpacing(10);
    this.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1;");
    // Load events after UI setup (if jwtToken is set later, call refreshEvents())
  }

  /**
   * Renders the calendar for a specific month using the provided event data.
   * @param yearMonth The month to display.
   * @param events A map where the key is a date and the value is a list of events for that date.
   */
  public void updateCalendar(YearMonth yearMonth, Map<LocalDate, List<CalendarEvent>> events) {
    System.out.println("updateCalendar called for: " + yearMonth + ", events.size=" + (events != null ? events.size() : 0));
    this.currentYearMonth = yearMonth;
    calendarGrid.getChildren().clear();
    calendarGrid.getColumnConstraints().clear();
    calendarGrid.getRowConstraints().clear();
    // Set column and row constraints for 7x7 grid
    for (int i = 0; i < 7; i++) {
      ColumnConstraints colConst = new ColumnConstraints();
      colConst.setPercentWidth(100.0 / 7);
      colConst.setFillWidth(true);
      calendarGrid.getColumnConstraints().add(colConst);
    }
    for (int i = 0; i < 7; i++) {
      RowConstraints rowConst = new RowConstraints();
      rowConst.setPercentHeight(100.0 / 7);
      rowConst.setFillHeight(true);
      calendarGrid.getRowConstraints().add(rowConst);
    }

    monthYearLabel.setText(currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + currentYearMonth.getYear());

    // Day headers
    String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (int i = 0; i < 7; i++) {
      Label dayHeader = new Label(dayNames[i]);
      dayHeader.setAlignment(Pos.CENTER);
      dayHeader.setFont(Font.font("System", FontWeight.BOLD, 12));
      dayHeader.setPrefSize(80, 30);
      dayHeader.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
      dayHeader.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1;");
      calendarGrid.add(dayHeader, i, 0);
      System.out.println("Added day header: " + dayNames[i] + " at col " + i);
    }

    LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
    int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;

    int dayNumber = 1;
    for (int row = 1; row <= 6; row++) {
      for (int col = 0; col < 7; col++) {
        if (row == 1 && col < dayOfWeek) {
          addEmptyCell(col, row);
          System.out.println("Added empty cell at (" + col + ", " + row + ")");
        } else if (dayNumber <= currentYearMonth.lengthOfMonth()) {
          LocalDate cellDate = currentYearMonth.atDay(dayNumber);
          List<CalendarEvent> dayEvents = events.getOrDefault(cellDate, Collections.emptyList());
          VBox dayCell = createDayCell(dayNumber, dayEvents);
          calendarGrid.add(dayCell, col, row);
          System.out.println("Added day cell for day " + dayNumber + " at (" + col + ", " + row + ")");
          dayNumber++;
        } else {
          addEmptyCell(col, row);
          System.out.println("Added empty cell at (" + col + ", " + row + ")");
        }
      }
    }
    System.out.println("calendarGrid children count after update: " + calendarGrid.getChildren().size());
  }

  private VBox createDayCell(int day, List<CalendarEvent> events) {
    VBox dayCell = new VBox(2);
    dayCell.setPrefSize(80, 60);
    dayCell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    dayCell.setAlignment(Pos.TOP_LEFT);
    dayCell.setPadding(new Insets(2));

    LocalDate cellDate = currentYearMonth.atDay(day);

    Label dayLabel = new Label(String.valueOf(day));
    dayLabel.setFont(Font.font("System", 12));

    if (cellDate.equals(LocalDate.now())) {
      dayCell.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: black; -fx-border-width: 2;");
      dayLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
    } else {
      dayCell.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1;");
    }

    dayCell.getChildren().add(dayLabel);

    dayCell.setOnMouseClicked(e -> {
      if (e.getButton() == MouseButton.PRIMARY) {
        selectedDate = cellDate;
        if (onDayClick != null) {
          onDayClick.accept(cellDate);
        }
      }
    });

    // Add context menu for events
    if (!events.isEmpty()) {
      for (CalendarEvent event : events) {
        Label eventLabel = new Label(event.getEventName());
        eventLabel.setFont(Font.font("System", 10));
        eventLabel.setTextFill(Color.DARKGREEN);
        eventLabel.setOnMouseClicked(ev -> {
          if (ev.getButton() == MouseButton.SECONDARY) {
            ContextMenu menu = new ContextMenu();
            MenuItem edit = new MenuItem("Edit");
            MenuItem delete = new MenuItem("Delete");
            edit.setOnAction(ae -> { if (onEditEvent != null) onEditEvent.accept(event); });
            delete.setOnAction(ae -> { if (onDeleteEvent != null) onDeleteEvent.accept(event); });
            menu.getItems().addAll(edit, delete);
            menu.show(eventLabel, ev.getScreenX(), ev.getScreenY());
          }
        });
        dayCell.getChildren().add(eventLabel);
      }
    }

    return dayCell;
  }

  private void addEmptyCell(int col, int row) {
    Label emptyLabel = new Label("");
    emptyLabel.setPrefSize(80, 60);
    emptyLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    emptyLabel.setStyle("-fx-background-color: #fafafa; -fx-border-color: black; -fx-border-width: 1;");
    calendarGrid.add(emptyLabel, col, row);
  }

  public void setOnMonthChangeListener(Consumer<YearMonth> listener) {
    this.onMonthChange = listener;
  }

  public void setOnDayClickListener(Consumer<LocalDate> listener) {
    this.onDayClick = listener;
  }

  public void setOnAddEventListener(Consumer<LocalDate> listener) { this.onAddEvent = listener; }

  public void setOnEditEventListener(Consumer<CalendarEvent> listener) { this.onEditEvent = listener; }

  public void setOnDeleteEventListener(Consumer<CalendarEvent> listener) { this.onDeleteEvent = listener; }

  public YearMonth getCurrentYearMonth() {
    return currentYearMonth;
  }

  public void setJwtToken(String token) {
    this.jwtToken = token;
  }

  public void loadAndDisplayEvents() {
    if (jwtToken == null) return;
    String eventsJson = com.studentapp.frontend.client.CalendarApiClient.getEvents(jwtToken);
    List<CalendarEvent> events = new Gson().fromJson(eventsJson, new TypeToken<List<CalendarEvent>>(){}.getType());
    Map<LocalDate, List<CalendarEvent>> eventsByDate = events.stream()
        .collect(Collectors.groupingBy(event -> event.getStartTime().toLocalDate()));
    updateCalendar(currentYearMonth, eventsByDate);
  }

  public void refreshEvents() {
    loadAndDisplayEvents();
  }
}