package com.studentapp.frontend.view;

import com.studentapp.common.model.CalendarEvent; // Use the common model
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
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
import java.util.Optional;
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
    if (events != null) {
      events.forEach((date, eventList) -> {
        System.out.println("Date: " + date + " has " + eventList.size() + " events");
      });
    }
    this.currentYearMonth = yearMonth;
    calendarGrid.getChildren().clear();
    calendarGrid.getColumnConstraints().clear();
    calendarGrid.getRowConstraints().clear();
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
    dayCell.setMinWidth(80);
    dayCell.setMinHeight(60);

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

    // Add event rectangles
    if (!events.isEmpty()) {
      System.out.println("Creating " + events.size() + " event rectangles for day " + day);
      VBox eventsContainer = new VBox(1);
      eventsContainer.setAlignment(Pos.TOP_LEFT);
      eventsContainer.setMaxWidth(Double.MAX_VALUE);
      VBox.setVgrow(eventsContainer, Priority.ALWAYS);
      
      for (CalendarEvent event : events) {
        StackPane eventRectangle = createEventRectangle(event, 72); // Use fixed width for now
        eventsContainer.getChildren().add(eventRectangle);
      }
      
      dayCell.getChildren().add(eventsContainer);
    }

    return dayCell;
  }

  private StackPane createEventRectangle(CalendarEvent event, double cellWidth) {
    // Create a rectangle for the event
    Rectangle rectangle = new Rectangle();
    rectangle.setHeight(14);
    rectangle.setWidth(cellWidth - 8); // Dynamic width based on cell
    rectangle.setArcWidth(3);
    rectangle.setArcHeight(3);
    
    // Set color based on event type
    Color eventColor = getEventTypeColor(event.getEventType());
    rectangle.setFill(eventColor);
    rectangle.setStroke(Color.BLACK);
    rectangle.setStrokeWidth(0.5);
    
    // Create text for the event name with ellipsis
    String displayText = event.getEventName();
    if (displayText.length() > 15) {
      displayText = displayText.substring(0, 12) + "...";
    }
    
    Text eventText = new Text(displayText);
    eventText.setFont(Font.font("System", 8));
    eventText.setFill(Color.WHITE);
    eventText.setWrappingWidth(cellWidth - 12); // Dynamic text wrapping
    
    // Create StackPane to overlay text on rectangle
    StackPane eventPane = new StackPane();
    eventPane.getChildren().addAll(rectangle, eventText);
    eventPane.setAlignment(Pos.CENTER_LEFT);
    eventPane.setPadding(new Insets(1));
    eventPane.setMaxWidth(Double.MAX_VALUE);
    
    // Add click handler for event details popup
    eventPane.setOnMouseClicked(ev -> {
      if (ev.getButton() == MouseButton.PRIMARY) {
        showEventDetailsPopup(event);
      } else if (ev.getButton() == MouseButton.SECONDARY) {
        ContextMenu menu = new ContextMenu();
        MenuItem edit = new MenuItem("Edit");
        MenuItem delete = new MenuItem("Delete");
        edit.setOnAction(ae -> { if (onEditEvent != null) onEditEvent.accept(event); });
        delete.setOnAction(ae -> { if (onDeleteEvent != null) onDeleteEvent.accept(event); });
        menu.getItems().addAll(edit, delete);
        menu.show(eventPane, ev.getScreenX(), ev.getScreenY());
      }
    });
    
    // Add hover effect
    eventPane.setOnMouseEntered(ev -> {
      rectangle.setStroke(Color.WHITE);
      rectangle.setStrokeWidth(1.5);
      eventPane.setCursor(javafx.scene.Cursor.HAND);
    });
    
    eventPane.setOnMouseExited(ev -> {
      rectangle.setStroke(Color.BLACK);
      rectangle.setStrokeWidth(0.5);
      eventPane.setCursor(javafx.scene.Cursor.DEFAULT);
    });
    
    return eventPane;
  }

  private Color getEventTypeColor(CalendarEvent.EventType eventType) {
    if (eventType == null) {
      return Color.GRAY;
    }
    
    switch (eventType) {
      case MEETING:
        return Color.BLUE;
      case PERSONAL:
        return Color.GREEN;
      case FINANCIAL:
        return Color.ORANGE;
      case APPOINTMENT:
        return Color.PURPLE;
      case OTHER:
      default:
        return Color.GRAY;
    }
  }

  private void showEventDetailsPopup(CalendarEvent event) {
    Dialog<CalendarEvent> dialog = new Dialog<>();
    dialog.setTitle("Event Details");
    dialog.setHeaderText(event.getEventName());
    dialog.setResizable(true);
    dialog.setWidth(500);
    dialog.setHeight(400);

    // Create custom dialog pane
    DialogPane dialogPane = dialog.getDialogPane();
    dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);

    // Create form layout
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    // Event Name
    Label nameLabel = new Label("Event Name:");
    TextField nameField = new TextField(event.getEventName());
    nameField.setEditable(false);
    nameField.setPrefWidth(300);
    grid.add(nameLabel, 0, 0);
    grid.add(nameField, 1, 0);

    // Description
    Label descLabel = new Label("Description:");
    TextArea descArea = new TextArea(event.getDescription() != null ? event.getDescription() : "");
    descArea.setEditable(false);
    descArea.setPrefRowCount(3);
    descArea.setPrefWidth(300);
    grid.add(descLabel, 0, 1);
    grid.add(descArea, 1, 1);

    // Start Time
    Label startLabel = new Label("Start Time:");
    TextField startField = new TextField(event.getStartTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    startField.setEditable(false);
    grid.add(startLabel, 0, 2);
    grid.add(startField, 1, 2);

    // End Time
    Label endLabel = new Label("End Time:");
    TextField endField = new TextField(event.getEndTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    endField.setEditable(false);
    grid.add(endLabel, 0, 3);
    grid.add(endField, 1, 3);

    // Event Type
    Label typeLabel = new Label("Event Type:");
    ComboBox<String> typeCombo = new ComboBox<>();
    typeCombo.getItems().addAll("MEETING", "PERSONAL", "FINANCIAL", "APPOINTMENT", "OTHER");
    typeCombo.setValue(event.getEventType().toString());
    typeCombo.setDisable(true);
    grid.add(typeLabel, 0, 4);
    grid.add(typeCombo, 1, 4);

    // Meeting Links
    Label linksLabel = new Label("Meeting Links:");
    TextField linksField = new TextField(event.getMeetingLinks() != null ? event.getMeetingLinks() : "");
    linksField.setEditable(false);
    grid.add(linksLabel, 0, 5);
    grid.add(linksField, 1, 5);

    // Create buttons
    Button editButton = new Button("Edit");
    editButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: white; -fx-font-weight: bold;");
    
    Button deleteButton = new Button("Delete");
    deleteButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-font-weight: bold;");

    // Button layout
    HBox buttonBox = new HBox(10);
    buttonBox.setAlignment(Pos.CENTER_RIGHT);
    buttonBox.getChildren().addAll(editButton, deleteButton);
    grid.add(buttonBox, 1, 6);

    // Edit button functionality
    editButton.setOnAction(e -> {
      boolean isEditing = !nameField.isEditable();
      if (isEditing) {
        // Enable editing
        nameField.setEditable(true);
        descArea.setEditable(true);
        startField.setEditable(true);
        endField.setEditable(true);
        typeCombo.setDisable(false);
        linksField.setEditable(true);
        editButton.setText("Save");
        editButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white; -fx-font-weight: bold;");
      } else {
        // Save changes
        try {
          event.setEventName(nameField.getText());
          event.setDescription(descArea.getText());
          event.setStartTime(java.time.LocalDateTime.parse(startField.getText(), 
              java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
          event.setEndTime(java.time.LocalDateTime.parse(endField.getText(), 
              java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
          event.setEventType(CalendarEvent.EventType.valueOf(typeCombo.getValue()));
          event.setMeetingLinks(linksField.getText());
          
          // Call the edit event handler
          if (onEditEvent != null) {
            onEditEvent.accept(event);
          }
          
          // Disable editing
          nameField.setEditable(false);
          descArea.setEditable(false);
          startField.setEditable(false);
          endField.setEditable(false);
          typeCombo.setDisable(true);
          linksField.setEditable(false);
          editButton.setText("Edit");
          editButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: white; -fx-font-weight: bold;");
          
        } catch (Exception ex) {
          Alert errorAlert = new Alert(AlertType.ERROR);
          errorAlert.setTitle("Error");
          errorAlert.setHeaderText("Invalid Input");
          errorAlert.setContentText("Please check your input format. Date format should be: yyyy-MM-dd HH:mm");
          errorAlert.showAndWait();
        }
      }
    });

    // Delete button functionality
    deleteButton.setOnAction(e -> {
      Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
      confirmAlert.setTitle("Confirm Delete");
      confirmAlert.setHeaderText("Delete Event");
      confirmAlert.setContentText("Are you sure you want to delete this event?");
      
      Optional<ButtonType> result = confirmAlert.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
        if (onDeleteEvent != null) {
          onDeleteEvent.accept(event);
        }
        dialog.close();
      }
    });

    dialogPane.setContent(grid);
    dialog.showAndWait();
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