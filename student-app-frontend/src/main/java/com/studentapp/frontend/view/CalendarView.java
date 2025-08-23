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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
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
   * 
   * New behavior: Clicking on a date directly opens the add event dialog.
   * The legacy "Add Event" button is hidden by default.
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

    monthYearLabel = new Label();
    monthYearLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
    monthYearLabel.setAlignment(Pos.CENTER);
    monthYearLabel.setMaxHeight(40);
    monthYearLabel.setMaxWidth(Double.MAX_VALUE); // Allow label to expand
    javafx.geometry.HPos hCenter = javafx.geometry.HPos.CENTER;
    javafx.scene.layout.GridPane.setHalignment(monthYearLabel, hCenter);

    Button previousMonthButton = new Button("←");
    previousMonthButton.setMinWidth(40);
    previousMonthButton.setPrefWidth(40);
    previousMonthButton.setOnAction(e -> {
      currentYearMonth = currentYearMonth.minusMonths(1);
      if (onMonthChange != null) {
        onMonthChange.accept(currentYearMonth);
      }
    });

    Button nextMonthButton = new Button("→");
    nextMonthButton.setMinWidth(40);
    nextMonthButton.setPrefWidth(40);
    nextMonthButton.setOnAction(e -> {
      currentYearMonth = currentYearMonth.plusMonths(1);
      if (onMonthChange != null) {
        onMonthChange.accept(currentYearMonth);
      }
    });

    Button goToButton = new Button("Go To");
    goToButton.setMinWidth(60);
    goToButton.setPrefWidth(60);
    goToButton.setOnAction(e -> showGoToMonthDialog());

    calendarGrid = new GridPane();
    calendarGrid.setHgap(2);
    calendarGrid.setVgap(4);
    calendarGrid.setAlignment(Pos.CENTER);
    VBox.setVgrow(calendarGrid, Priority.ALWAYS);
    calendarGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    calendarGrid.setMinHeight(400);
    calendarGrid.setStyle("-fx-background-color: #f8f8f8;");

    // Use GridPane for consistent navigation layout
    GridPane navigationGrid = new GridPane();
    navigationGrid.setHgap(10);
    navigationGrid.setVgap(5);
    navigationGrid.setAlignment(Pos.CENTER);
    
    // Add column constraints for consistent positioning
    ColumnConstraints leftCol = new ColumnConstraints();
    leftCol.setPrefWidth(40);
    leftCol.setMinWidth(40);
    leftCol.setMaxWidth(40);
    
    ColumnConstraints centerCol = new ColumnConstraints();
    centerCol.setPrefWidth(200);
    centerCol.setMinWidth(200);
    centerCol.setHgrow(Priority.ALWAYS);
    
    ColumnConstraints rightCol = new ColumnConstraints();
    rightCol.setPrefWidth(40);
    rightCol.setMinWidth(40);
    rightCol.setMaxWidth(40);
    
    ColumnConstraints goToCol = new ColumnConstraints();
    goToCol.setPrefWidth(60);
    goToCol.setMinWidth(60);
    goToCol.setMaxWidth(60);
    
    navigationGrid.getColumnConstraints().addAll(leftCol, centerCol, rightCol, goToCol);
    
    navigationGrid.add(previousMonthButton, 0, 0);
    navigationGrid.add(monthYearLabel, 1, 0);
    navigationGrid.add(nextMonthButton, 2, 0);
    navigationGrid.add(goToButton, 3, 0);
    addEventButton = new Button("Add Event (Legacy)");
    addEventButton.setOnAction(e -> {
      if (onAddEvent != null && selectedDate != null) {
        onAddEvent.accept(selectedDate);
      }
    });
    addEventButton.setVisible(false);

    VBox.setMargin(navigationGrid, new Insets(16, 0, 24, 0));
    VBox.setVgrow(calendarGrid, Priority.ALWAYS);

    this.getChildren().addAll(navigationGrid, calendarGrid, addEventButton);
    this.setFillWidth(true);
    this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    this.setPadding(new Insets(4, 16, 16, 16)); // Minimal top padding, others unchanged
    this.setSpacing(0); // Let margins handle spacing
    this.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1;");
  }

  /**
   * Renders the calendar for a specific month using the provided event data.
   * @param yearMonth The month to display.
   * @param events A map where the key is a date and the value is a list of events for that date.
   */
  public void updateCalendar(YearMonth yearMonth, Map<LocalDate, List<CalendarEvent>> events) {
    System.out.println("[DEBUG] updateCalendar called for: " + yearMonth + ", events.size=" + (events != null ? events.size() : 0));
    if (events != null) {
      events.forEach((date, eventList) -> {
        System.out.println("[DEBUG] Date: " + date + " has " + eventList.size() + " events");
      });
    }
    this.currentYearMonth = yearMonth;
    calendarGrid.getChildren().clear();
    calendarGrid.getColumnConstraints().clear();
    calendarGrid.getRowConstraints().clear();
    
    // Make columns responsive - each column takes equal width
    for (int i = 0; i < 7; i++) {
      ColumnConstraints colConst = new ColumnConstraints();
      colConst.setPercentWidth(100.0 / 7);
      colConst.setFillWidth(true);
      colConst.setMinWidth(80); // Minimum width for readability
      calendarGrid.getColumnConstraints().add(colConst);
    }
    
    // Make rows responsive - header row and 6 data rows
    for (int i = 0; i < 7; i++) {
      RowConstraints rowConst = new RowConstraints();
      if (i == 0) {
        // Header row - reduced height
        rowConst.setPrefHeight(24);
        rowConst.setMinHeight(24);
      } else {
        // Data rows - expand to fill available space
        rowConst.setPercentHeight(100.0 / 6);
        rowConst.setFillHeight(true);
        rowConst.setMinHeight(80); // Minimum height for events
      }
      calendarGrid.getRowConstraints().add(rowConst);
    }

    monthYearLabel.setText(currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + currentYearMonth.getYear());

    String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (int i = 0; i < 7; i++) {
      Label dayHeader = new Label(dayNames[i]);
      dayHeader.setAlignment(Pos.CENTER);
      dayHeader.setFont(Font.font("System", FontWeight.BOLD, 12));
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
        } else if (dayNumber <= currentYearMonth.lengthOfMonth()) {
          LocalDate cellDate = currentYearMonth.atDay(dayNumber);
          List<CalendarEvent> dayEvents = events.getOrDefault(cellDate, Collections.emptyList());
          VBox dayCell = createDayCell(dayNumber, dayEvents);
          calendarGrid.add(dayCell, col, row);
          dayNumber++;
        } else {
          addEmptyCell(col, row);
        }
      }
    }
  }

  private VBox createDayCell(int day, List<CalendarEvent> events) {
    VBox dayCell = new VBox(2);
    dayCell.setPrefSize(100, 60);
    dayCell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    dayCell.setAlignment(Pos.TOP_LEFT);
    dayCell.setPadding(new Insets(2));
    dayCell.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1;");

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

    // Add hover effects to indicate clickability
    dayCell.setOnMouseEntered(e -> {
      dayCell.setCursor(javafx.scene.Cursor.HAND);
      dayCell.setStyle("-fx-background-color: #f0f8ff; -fx-border-color: #4CAF50; -fx-border-width: 2;");
    });
    
    dayCell.setOnMouseExited(e -> {
      dayCell.setCursor(javafx.scene.Cursor.DEFAULT);
      if (cellDate.equals(LocalDate.now())) {
        dayCell.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: black; -fx-border-width: 2;");
      } else {
        dayCell.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1;");
      }
    });

    dayCell.setOnMouseClicked(e -> {
      if (e.getButton() == MouseButton.PRIMARY) {
        selectedDate = cellDate;
        // Directly trigger add event when a date is clicked
        if (onAddEvent != null) {
          onAddEvent.accept(cellDate);
        }
        // Also call the day click listener for any other functionality
        if (onDayClick != null) {
          onDayClick.accept(cellDate);
        }
      }
    });

    // Add event rectangles with scrolling
    if (!events.isEmpty()) {
      System.out.println("[DEBUG] Adding " + events.size() + " events for day " + day);
      
      // Create a scrollable container for events
      javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
      scrollPane.setMaxWidth(Double.MAX_VALUE);
      scrollPane.setMaxHeight(100); // Limit height to prevent taking all space
      scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
      scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
      scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
      scrollPane.setPadding(new Insets(0));
      scrollPane.setFitToWidth(true);
      
      VBox eventsContainer = new VBox(2);
      eventsContainer.setAlignment(Pos.TOP_LEFT);
      eventsContainer.setMaxWidth(Double.MAX_VALUE);
      eventsContainer.setPadding(new Insets(2));
      eventsContainer.setStyle("-fx-background-color: transparent;");
      
      for (CalendarEvent event : events) {
        StackPane eventRectangle = createEventRectangle(event);
        eventsContainer.getChildren().add(eventRectangle);
        System.out.println("[DEBUG] Added event rectangle for: " + event.getEventName());
      }
      
      scrollPane.setContent(eventsContainer);
      VBox.setVgrow(scrollPane, Priority.NEVER); // Don't grow to fill all space
      dayCell.getChildren().add(scrollPane);
    } else {
      System.out.println("[DEBUG] No events for day " + day);
    }

    return dayCell;
  }

  private StackPane createEventRectangle(CalendarEvent event) {
    System.out.println("[DEBUG] Creating event rectangle for: " + event.getEventName() + " with type: " + event.getEventType());
    
    // Create a rectangle for the event
    Rectangle rectangle = new Rectangle();
    rectangle.setHeight(18);
    rectangle.setArcWidth(4);
    rectangle.setArcHeight(4);
    
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
    eventText.setFont(Font.font("System", 9));
    eventText.setFill(Color.WHITE);
    
    // Create StackPane to overlay text on rectangle
    StackPane eventPane = new StackPane();
    eventPane.getChildren().addAll(rectangle, eventText);
    eventPane.setAlignment(Pos.CENTER_LEFT);
    eventPane.setPadding(new Insets(1));
    eventPane.setMaxWidth(Double.MAX_VALUE);
    eventPane.setMinHeight(18);
    eventPane.setPrefHeight(18);
    
    // Bind rectangle width to pane width
    rectangle.widthProperty().bind(eventPane.widthProperty().subtract(4));
    eventText.setWrappingWidth(0); // Let text wrap naturally
    
    // Add click handler for event details popup
    eventPane.setOnMouseClicked(ev -> {
      // Stop event propagation to prevent the day cell click handler from firing
      ev.consume();
      
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
      // Stop event propagation to prevent day cell hover effects
      ev.consume();
      rectangle.setStroke(Color.WHITE);
      rectangle.setStrokeWidth(1.5);
      eventPane.setCursor(javafx.scene.Cursor.HAND);
      eventPane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 0, 1);");
    });
    
    eventPane.setOnMouseExited(ev -> {
      // Stop event propagation to prevent day cell hover effects
      ev.consume();
      rectangle.setStroke(Color.BLACK);
      rectangle.setStrokeWidth(0.5);
      eventPane.setCursor(javafx.scene.Cursor.DEFAULT);
      eventPane.setStyle("");
    });
    
    return eventPane;
  }

  private Color getEventTypeColor(CalendarEvent.EventType eventType) {
    if (eventType == null) {
      return Color.GRAY;
    }
    
    switch (eventType) {
      case MEETING:
        return Color.rgb(52, 152, 219); 
      case PERSONAL:
        return Color.rgb(46, 204, 113); 
      case FINANCIAL:
        return Color.rgb(230, 126, 34);
      case APPOINTMENT:
        return Color.rgb(155, 89, 182); 
      case OTHER:
      default:
        return Color.rgb(149, 165, 166); 
    }
  }

  private void showEventDetailsPopup(CalendarEvent event) {
    Dialog<CalendarEvent> dialog = new Dialog<>();
    dialog.setTitle(event.getEventName().toString());
    dialog.setResizable(true);
    dialog.setWidth(500);
    dialog.setHeight(500);

    DialogPane dialogPane = dialog.getDialogPane();
    // Restore the default close button
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
    editButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-border-color:rgb(109, 109, 109); -fx-border-width: 2;");
    
    Button deleteButton = new Button("Delete");
    deleteButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-font-weight: bold;");

    // Button layout
    HBox buttonBox = new HBox(10);
    buttonBox.setAlignment(Pos.CENTER_RIGHT);
    buttonBox.getChildren().addAll(editButton, deleteButton);
    grid.add(buttonBox, 1, 6);

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
        editButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #32CD32; -fx-border-width: 2;");
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
          editButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-border-color:rgb(109, 109, 109); -fx-border-width: 2;");
          
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
      // confirmAlert.setHeaderText("Delete Event");
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
    emptyLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    emptyLabel.setStyle("-fx-background-color: #fafafa; -fx-border-color: black; -fx-border-width: 1;");
    calendarGrid.add(emptyLabel, col, row);
  }

  private void showGoToMonthDialog() {
    Dialog<YearMonth> dialog = new Dialog<>();
    dialog.setTitle("Go to Month");
    dialog.setResizable(false);
    dialog.setWidth(300);
    dialog.setHeight(200);

    DialogPane dialogPane = dialog.getDialogPane();
    dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    // Create form layout
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 20, 10, 20));

    // Month selection
    Label monthLabel = new Label("Month:");
    ComboBox<String> monthCombo = new ComboBox<>();
    monthCombo.getItems().addAll(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    );
    monthCombo.setValue(currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
    monthCombo.setPrefWidth(150);
    grid.add(monthLabel, 0, 0);
    grid.add(monthCombo, 1, 0);

    // Year selection
    Label yearLabel = new Label("Year:");
    ComboBox<Integer> yearCombo = new ComboBox<>();
    int currentYear = currentYearMonth.getYear();
    for (int year = currentYear - 10; year <= currentYear + 10; year++) {
      yearCombo.getItems().add(year);
    }
    yearCombo.setValue(currentYear);
    yearCombo.setPrefWidth(150);
    grid.add(yearLabel, 0, 1);
    grid.add(yearCombo, 1, 1);

    dialogPane.setContent(grid);

    // Set result converter
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == ButtonType.OK) {
        try {
          String selectedMonth = monthCombo.getValue();
          Integer selectedYear = yearCombo.getValue();
          
          if (selectedMonth != null && selectedYear != null) {
            // Convert month name to month number
            java.time.Month month = java.time.Month.valueOf(selectedMonth.toUpperCase());
            return YearMonth.of(selectedYear, month);
          }
        } catch (Exception e) {
          Alert errorAlert = new Alert(AlertType.ERROR);
          errorAlert.setTitle("Error");
          errorAlert.setHeaderText("Invalid Selection");
          errorAlert.setContentText("Please select valid month and year.");
          errorAlert.showAndWait();
        }
      }
      return null;
    });

    Optional<YearMonth> result = dialog.showAndWait();
    result.ifPresent(yearMonth -> {
      currentYearMonth = yearMonth;
      if (onMonthChange != null) {
        onMonthChange.accept(currentYearMonth);
      }
    });
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

  /**
   * Optionally show the legacy "Add Event" button.
   * By default, the button is hidden since clicking on dates directly opens the add event dialog.
   */
  public void setShowLegacyAddButton(boolean show) {
    addEventButton.setVisible(show);
  }

  public YearMonth getCurrentYearMonth() {
    return currentYearMonth;
  }

  public void setJwtToken(String token) {
    this.jwtToken = token;
  }

  public void loadAndDisplayEvents() {
    if (jwtToken == null) {
      System.out.println("[DEBUG] JWT token is null, cannot load events");
      return;
    }
    System.out.println("[DEBUG] Loading events with JWT token: " + jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...");
    
    String eventsJson = com.studentapp.frontend.client.CalendarApiClient.getEvents(jwtToken);
    System.out.println("[DEBUG] Received events JSON: " + eventsJson);
    
    List<CalendarEvent> events = new Gson().fromJson(eventsJson, new TypeToken<List<CalendarEvent>>(){}.getType());
    System.out.println("[DEBUG] Parsed " + (events != null ? events.size() : 0) + " events");
    
    if (events != null) {
      Map<LocalDate, List<CalendarEvent>> eventsByDate = events.stream()
          .collect(Collectors.groupingBy(event -> event.getStartTime().toLocalDate()));
      System.out.println("[DEBUG] Grouped events by date: " + eventsByDate.size() + " dates have events");
      eventsByDate.forEach((date, eventList) -> {
        System.out.println("[DEBUG] Date " + date + " has " + eventList.size() + " events");
      });
      updateCalendar(currentYearMonth, eventsByDate);
    } else {
      System.out.println("[DEBUG] Events list is null, updating calendar with empty map");
      updateCalendar(currentYearMonth, Collections.emptyMap());
    }
  }

  public void refreshEvents() {
    loadAndDisplayEvents();
  }
}