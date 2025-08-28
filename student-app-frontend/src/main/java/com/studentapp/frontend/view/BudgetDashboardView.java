package com.studentapp.frontend.view;

import com.studentapp.common.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Budget Dashboard View that displays budget analysis, spending trends, and financial goals.
 * This is the main financial overview and planning interface.
 */
public class BudgetDashboardView extends VBox {

    // UI Components
    private TableView<BudgetLimit> budgetTable;
    private TableView<FinancialGoal> goalTable;
    private PieChart categoryPieChart;
    private LineChart<String, Number> spendingTrendChart;
    private Label totalSpentLabel;
    private Label budgetRemainingLabel;
    private Label totalBudgetLabel;
    private Label monthYearLabel;
    private ComboBox<String> monthComboBox;
    private ComboBox<Integer> yearComboBox;
    private ListView<String> alertsListView;
    private ProgressBar budgetProgressBar;

    // Action handlers
    private Consumer<BudgetLimit> onAddBudget;
    private Consumer<BudgetLimit> onEditBudget;
    private Consumer<String> onDeleteBudget;
    private Consumer<FinancialGoal> onAddGoal;
    private Consumer<FinancialGoal> onEditGoal;
    private Consumer<String> onDeleteGoal;
    private Runnable onRefreshData;

    public BudgetDashboardView() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        // Month/Year selection
        monthComboBox = new ComboBox<>();
        monthComboBox.getItems().addAll(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        );
        // Select current month by index to avoid text mismatches
        monthComboBox.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        yearComboBox = new ComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 2; i++) {
            yearComboBox.getItems().add(i);
        }
        yearComboBox.setValue(currentYear);

        // Summary labels
        totalSpentLabel = new Label("$0.00");
        totalSpentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        totalSpentLabel.setTextFill(Color.DARKRED);

        budgetRemainingLabel = new Label("$0.00");
        budgetRemainingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        budgetRemainingLabel.setTextFill(Color.DARKGREEN);

        totalBudgetLabel = new Label("$0.00");
        totalBudgetLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        totalBudgetLabel.setTextFill(Color.DARKBLUE);

        budgetProgressBar = new ProgressBar(0);
        budgetProgressBar.setPrefWidth(300);
        budgetProgressBar.setPrefHeight(20);

        monthYearLabel = new Label("Budget Dashboard");
        monthYearLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Budget Table
        budgetTable = new TableView<>();
        setupBudgetTable();

        // Goals Table
        goalTable = new TableView<>();
        setupGoalTable();

        // Charts - Initialize with sample data
        categoryPieChart = new PieChart();
        categoryPieChart.setTitle("Budget Allocation by Category");
        categoryPieChart.setPrefSize(400, 300);

        // Add initial sample data to pie chart to ensure it displays
        ObservableList<PieChart.Data> initialPieData = FXCollections.observableArrayList();
        initialPieData.add(new PieChart.Data("Food & Dining", 200));
        initialPieData.add(new PieChart.Data("Transportation", 150));
        initialPieData.add(new PieChart.Data("Entertainment", 100));
        categoryPieChart.setData(initialPieData);

        setupSpendingTrendChart();

        // Alerts
        alertsListView = new ListView<>();
        alertsListView.setPrefHeight(120);
    }

    private void setupBudgetTable() {
        TableColumn<BudgetLimit, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCategory() != null ?
                cellData.getValue().getCategory().getName() : ""));
        categoryCol.setPrefWidth(150);

        TableColumn<BudgetLimit, BigDecimal> limitCol = new TableColumn<>("Budget Limit");
        limitCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("limitAmount"));
        limitCol.setPrefWidth(120);
        limitCol.setCellFactory(col -> new TableCell<BudgetLimit, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText("$" + amount.toString());
                }
            }
        });

        TableColumn<BudgetLimit, BigDecimal> thresholdCol = new TableColumn<>("Alert %");
        thresholdCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("alertThreshold"));
        thresholdCol.setPrefWidth(80);
        thresholdCol.setCellFactory(col -> new TableCell<BudgetLimit, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal threshold, boolean empty) {
                super.updateItem(threshold, empty);
                if (empty || threshold == null) {
                    setText(null);
                } else {
                    setText(threshold.toString() + "%");
                }
            }
        });

        TableColumn<BudgetLimit, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> {
            // This would be calculated based on actual spending vs budget
            return new javafx.beans.property.SimpleStringProperty("On Track");
        });
        statusCol.setPrefWidth(100);

        budgetTable.getColumns().addAll(categoryCol, limitCol, thresholdCol, statusCol);
        budgetTable.setRowFactory(tv -> {
            TableRow<BudgetLimit> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit Budget");
            editItem.setOnAction(e -> {
                if (onEditBudget != null && row.getItem() != null) {
                    onEditBudget.accept(row.getItem());
                }
            });

            MenuItem deleteItem = new MenuItem("Delete Budget");
            deleteItem.setOnAction(e -> {
                if (onDeleteBudget != null && row.getItem() != null) {
                    onDeleteBudget.accept(row.getItem().getId().toString());
                }
            });

            contextMenu.getItems().addAll(editItem, deleteItem);
            row.setContextMenu(contextMenu);
            return row;
        });
    }

    private void setupGoalTable() {
        TableColumn<FinancialGoal, String> nameCol = new TableColumn<>("Goal Name");
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("goalName"));
        nameCol.setPrefWidth(150);

        TableColumn<FinancialGoal, BigDecimal> targetCol = new TableColumn<>("Target");
        targetCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("targetAmount"));
        targetCol.setPrefWidth(100);
        targetCol.setCellFactory(col -> new TableCell<FinancialGoal, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText("$" + amount.toString());
                }
            }
        });

        TableColumn<FinancialGoal, BigDecimal> currentCol = new TableColumn<>("Current");
        currentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("currentAmount"));
        currentCol.setPrefWidth(100);
        currentCol.setCellFactory(col -> new TableCell<FinancialGoal, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText("$" + amount.toString());
                }
            }
        });

        TableColumn<FinancialGoal, String> progressCol = new TableColumn<>("Progress");
        progressCol.setCellValueFactory(cellData -> {
            FinancialGoal goal = cellData.getValue();
            if (goal != null) {
                BigDecimal progress = goal.getProgressPercentage();
                return new javafx.beans.property.SimpleStringProperty(progress.toString() + "%");
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        progressCol.setPrefWidth(80);

        goalTable.getColumns().addAll(nameCol, targetCol, currentCol, progressCol);
        goalTable.setRowFactory(tv -> {
            TableRow<FinancialGoal> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit Goal");
            editItem.setOnAction(e -> {
                if (onEditGoal != null && row.getItem() != null) {
                    onEditGoal.accept(row.getItem());
                }
            });

            MenuItem deleteItem = new MenuItem("Delete Goal");
            deleteItem.setOnAction(e -> {
                if (onDeleteGoal != null && row.getItem() != null) {
                    onDeleteGoal.accept(row.getItem().getId().toString());
                }
            });

            contextMenu.getItems().addAll(editItem, deleteItem);
            row.setContextMenu(contextMenu);
            return row;
        });
    }

    private void setupSpendingTrendChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount ($)");
        yAxis.setForceZeroInRange(false);

        spendingTrendChart = new LineChart<>(xAxis, yAxis);
        spendingTrendChart.setTitle("Spending Trends vs Budget");
        spendingTrendChart.setAnimated(false);
        spendingTrendChart.setPrefSize(600, 300);

        // Add initial sample data to line chart to ensure it displays
        XYChart.Series<String, Number> initialSpendingSeries = new XYChart.Series<>();
        initialSpendingSeries.setName("Actual Spending");

        XYChart.Series<String, Number> initialBudgetSeries = new XYChart.Series<>();
        initialBudgetSeries.setName("Budget Limit");

        // Add 6 months of sample data
        String[] months = {"Mar 2025", "Apr 2025", "May 2025", "Jun 2025", "Jul 2025", "Aug 2025"};
        double[] spending = {220, 180, 250, 200, 190, 240};
        double[] budget = {350, 350, 350, 350, 350, 350};

        for (int i = 0; i < months.length; i++) {
            initialSpendingSeries.getData().add(new XYChart.Data<>(months[i], spending[i]));
            initialBudgetSeries.getData().add(new XYChart.Data<>(months[i], budget[i]));
        }

        spendingTrendChart.getData().addAll(initialSpendingSeries, initialBudgetSeries);
    }

    private void setupLayout() {
        setSpacing(15);
        setPadding(new Insets(20));

        // Header with title and date selection
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(
            monthYearLabel,
            new Label("Month:"), monthComboBox,
            new Label("Year:"), yearComboBox
        );

        // Summary dashboard
        GridPane summaryGrid = createSummaryDashboard();

        // Action buttons
        HBox buttonBox = createActionButtons();

        // Budget alerts section
        VBox alertsSection = new VBox(5);
        Label alertsTitle = new Label("Budget Alerts & Notifications");
        alertsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        alertsSection.getChildren().addAll(alertsTitle, alertsListView);

        // Charts section
        HBox chartsBox = new HBox(20);
        chartsBox.getChildren().addAll(categoryPieChart, spendingTrendChart);

        // Tables in tabs
        TabPane tabPane = new TabPane();

        Tab budgetsTab = new Tab("Budget Limits");
        budgetsTab.setClosable(false);
        budgetsTab.setContent(new VBox(10, createBudgetButtons(), budgetTable));

        Tab goalsTab = new Tab("Financial Goals");
        goalsTab.setClosable(false);
        goalsTab.setContent(new VBox(10, createGoalButtons(), goalTable));

        tabPane.getTabs().addAll(budgetsTab, goalsTab);

        getChildren().addAll(header, summaryGrid, buttonBox, alertsSection, chartsBox, tabPane);
    }

    private GridPane createSummaryDashboard() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(15));
        grid.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #e9ecef; -fx-border-radius: 10;");

        // Total Budget Card
        VBox budgetCard = createSummaryCard("Total Budget", totalBudgetLabel, Color.DARKBLUE);
        grid.add(budgetCard, 0, 0);

        // Total Spent Card
        VBox spentCard = createSummaryCard("Total Spent", totalSpentLabel, Color.DARKRED);
        grid.add(spentCard, 1, 0);

        // Budget Remaining Card
        VBox remainingCard = createSummaryCard("Remaining", budgetRemainingLabel, Color.DARKGREEN);
        grid.add(remainingCard, 2, 0);

        // Budget Progress Bar
        VBox progressCard = new VBox(5);
        progressCard.setAlignment(Pos.CENTER);
        Label progressLabel = new Label("Budget Usage");
        progressLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        progressCard.getChildren().addAll(progressLabel, budgetProgressBar);
        grid.add(progressCard, 0, 1, 3, 1);

        return grid;
    }

    private VBox createSummaryCard(String title, Label valueLabel, Color color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(150);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #dee2e6; -fx-border-radius: 5;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        titleLabel.setTextFill(Color.GRAY);

        valueLabel.setTextFill(color);

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private HBox createActionButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button refreshButton = new Button("Refresh Dashboard");
        refreshButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold;");
        refreshButton.setOnAction(e -> {
            if (onRefreshData != null) {
                onRefreshData.run();
            }
        });

        buttonBox.getChildren().add(refreshButton);
        return buttonBox;
    }

    private HBox createBudgetButtons() {
        HBox buttonBox = new HBox(10);

        Button addButton = new Button("Set Budget Limit");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        addButton.setOnAction(e -> {
            if (onAddBudget != null) {
                onAddBudget.accept(new BudgetLimit());
            }
        });

        buttonBox.getChildren().add(addButton);
        return buttonBox;
    }

    private HBox createGoalButtons() {
        HBox buttonBox = new HBox(10);

        Button addButton = new Button("Add Financial Goal");
        addButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
        addButton.setOnAction(e -> {
            if (onAddGoal != null) {
                onAddGoal.accept(new FinancialGoal());
            }
        });

        buttonBox.getChildren().add(addButton);
        return buttonBox;
    }

    private void setupEventHandlers() {
        monthComboBox.setOnAction(e -> {
            if (onRefreshData != null) {
                onRefreshData.run();
            }
        });

        yearComboBox.setOnAction(e -> {
            if (onRefreshData != null) {
                onRefreshData.run();
            }
        });
    }

    // Public methods for updating data
    public void updateBudgetLimits(List<BudgetLimit> budgets) {
        budgetTable.setItems(FXCollections.observableArrayList(budgets));
    }

    public void updateFinancialGoals(List<FinancialGoal> goals) {
        goalTable.setItems(FXCollections.observableArrayList(goals));
    }

    public void updateCategoryChart(Map<String, BigDecimal> categoryData, Map<String, String> colorByCategory) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        categoryData.forEach((category, amount) -> pieChartData.add(new PieChart.Data(category, amount.doubleValue())));
        categoryPieChart.setData(pieChartData);
        // Apply colors per slice
        for (PieChart.Data data : categoryPieChart.getData()) {
            String name = data.getName();
            String hex = colorByCategory != null ? colorByCategory.get(name) : null;
            if (hex == null || hex.isBlank()) {
                if ("Uncategorized".equalsIgnoreCase(name)) hex = "#9E9E9E"; // grey
                else if ("No data".equalsIgnoreCase(name)) hex = "#BDBDBD";
                else hex = "#6c757d"; // default muted
            }
            applyPieSliceColor(data, hex);
        }
    }

    // Backward-compatible method (no colors provided)
    public void updateCategoryChart(Map<String, BigDecimal> categoryData) {
        updateCategoryChart(categoryData, new HashMap<>());
    }

    private void applyPieSliceColor(PieChart.Data data, String colorHex) {
        String hex = normalizeHex(colorHex);
        Runnable styler = () -> {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-pie-color: " + hex + ";");
            }
        };
        if (data.getNode() != null) styler.run();
        data.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                newNode.setStyle("-fx-pie-color: " + hex + ";");
            }
        });
    }

    private String normalizeHex(String c) {
        if (c == null) return "#6c757d";
        String s = c.trim();
        if (!s.startsWith("#")) s = "#" + s;
        // Accept #RGB or #RRGGBB; otherwise default
        if (s.length() == 4 || s.length() == 7) return s;
        return "#6c757d";
    }

    public void updateSpendingTrendChart(List<Map<String, Object>> monthlyData) {
        XYChart.Series<String, Number> spendingSeries = new XYChart.Series<>();
        spendingSeries.setName("Actual Spending");

        XYChart.Series<String, Number> budgetSeries = new XYChart.Series<>();
        budgetSeries.setName("Budget Limit");

        for (Map<String, Object> data : monthlyData) {
            String month = (String) data.get("month");
            BigDecimal total = (BigDecimal) data.get("total");
            BigDecimal budget = (BigDecimal) data.getOrDefault("budget", BigDecimal.valueOf(300)); // Sample budget

            spendingSeries.getData().add(new XYChart.Data<>(month, total.doubleValue()));
            budgetSeries.getData().add(new XYChart.Data<>(month, budget.doubleValue()));
        }

        spendingTrendChart.getData().clear();
        spendingTrendChart.getData().addAll(spendingSeries, budgetSeries);
    }

    public void updateSummary(BigDecimal totalBudget, BigDecimal totalSpent, BigDecimal budgetRemaining) {
        totalBudgetLabel.setText("$" + totalBudget.toString());
        totalSpentLabel.setText("$" + totalSpent.toString());
        budgetRemainingLabel.setText("$" + budgetRemaining.toString());

        // Update progress bar
        if (totalBudget.compareTo(BigDecimal.ZERO) > 0) {
            double progress = totalSpent.doubleValue() / totalBudget.doubleValue();
            budgetProgressBar.setProgress(Math.min(progress, 1.0));

            // Color coding for progress bar
            if (progress > 0.9) {
                budgetProgressBar.setStyle("-fx-accent: #dc3545;"); // Red for over 90%
            } else if (progress > 0.75) {
                budgetProgressBar.setStyle("-fx-accent: #ffc107;"); // Yellow for over 75%
            } else {
                budgetProgressBar.setStyle("-fx-accent: #28a745;"); // Green for under 75%
            }
        }

        // Color coding based on budget status
        if (budgetRemaining.compareTo(BigDecimal.ZERO) < 0) {
            budgetRemainingLabel.setTextFill(Color.DARKRED);
        } else {
            budgetRemainingLabel.setTextFill(Color.DARKGREEN);
        }
    }

    public void updateAlerts(List<String> alerts) {
        alertsListView.setItems(FXCollections.observableArrayList(alerts));
    }

    public int getSelectedMonth() {
        return monthComboBox.getSelectionModel().getSelectedIndex() + 1;
    }

    public int getSelectedYear() {
        return yearComboBox.getValue();
    }

    // Setters for action handlers
    public void setOnAddBudget(Consumer<BudgetLimit> onAddBudget) { this.onAddBudget = onAddBudget; }
    public void setOnEditBudget(Consumer<BudgetLimit> onEditBudget) { this.onEditBudget = onEditBudget; }
    public void setOnDeleteBudget(Consumer<String> onDeleteBudget) { this.onDeleteBudget = onDeleteBudget; }
    public void setOnAddGoal(Consumer<FinancialGoal> onAddGoal) { this.onAddGoal = onAddGoal; }
    public void setOnEditGoal(Consumer<FinancialGoal> onEditGoal) { this.onEditGoal = onEditGoal; }
    public void setOnDeleteGoal(Consumer<String> onDeleteGoal) { this.onDeleteGoal = onDeleteGoal; }
    public void setOnRefreshData(Runnable onRefreshData) { this.onRefreshData = onRefreshData; }
}
