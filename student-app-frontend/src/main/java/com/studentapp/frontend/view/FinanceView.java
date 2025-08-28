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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Main Finance Dashboard View that displays expense tracking, budget analysis, and financial goals.
 * This view is managed by a controller and displays data provided to it.
 */
public class FinanceView extends VBox {

    // UI Components
    private TableView<Expense> expenseTable;
    private TableView<ExpenseCategory> categoryTable;
    private TableView<BudgetLimit> budgetTable;
    private TableView<FinancialGoal> goalTable;
    private PieChart categoryPieChart;
    private LineChart<String, Number> spendingTrendChart;
    private Label totalSpentLabel;
    private Label budgetRemainingLabel;
    private Label monthYearLabel;
    private ComboBox<String> monthComboBox;
    private ComboBox<Integer> yearComboBox;
    private ListView<String> alertsListView;

    // Action handlers
    private Consumer<Expense> onAddExpense;
    private Consumer<Expense> onEditExpense;
    private Consumer<String> onDeleteExpense;
    private Consumer<ExpenseCategory> onAddCategory;
    private Consumer<ExpenseCategory> onEditCategory;
    private Consumer<String> onDeleteCategory;
    private Consumer<BudgetLimit> onAddBudget;
    private Consumer<BudgetLimit> onEditBudget;
    private Consumer<String> onDeleteBudget;
    private Consumer<FinancialGoal> onAddGoal;
    private Consumer<FinancialGoal> onEditGoal;
    private Consumer<String> onDeleteGoal;
    private Runnable onRefreshData;

    public FinanceView() {
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
        monthComboBox.setValue(LocalDate.now().getMonth().name().substring(0, 1) +
                              LocalDate.now().getMonth().name().substring(1).toLowerCase());

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

        monthYearLabel = new Label("Finance Dashboard");
        monthYearLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Expense Table
        expenseTable = new TableView<>();
        setupExpenseTable();

        // Category Table
        categoryTable = new TableView<>();
        setupCategoryTable();

        // Budget Table
        budgetTable = new TableView<>();
        setupBudgetTable();

        // Goals Table
        goalTable = new TableView<>();
        setupGoalTable();

        // Charts
        categoryPieChart = new PieChart();
        categoryPieChart.setTitle("Expenses by Category");
        categoryPieChart.setPrefSize(400, 300);

        setupSpendingTrendChart();

        // Alerts
        alertsListView = new ListView<>();
        alertsListView.setPrefHeight(100);
    }

    private void setupExpenseTable() {
        TableColumn<Expense, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(150);

        TableColumn<Expense, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCategory() != null ?
                cellData.getValue().getCategory().getName() : ""));
        categoryCol.setPrefWidth(120);

        TableColumn<Expense, BigDecimal> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(80);
        amountCol.setCellFactory(col -> new TableCell<Expense, BigDecimal>() {
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

        TableColumn<Expense, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("expenseDate"));
        dateCol.setPrefWidth(100);
        dateCol.setCellFactory(col -> new TableCell<Expense, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                }
            }
        });

        TableColumn<Expense, String> paymentCol = new TableColumn<>("Payment");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentCol.setPrefWidth(100);

        expenseTable.getColumns().addAll(titleCol, categoryCol, amountCol, dateCol, paymentCol);
        expenseTable.setRowFactory(tv -> {
            TableRow<Expense> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(e -> {
                if (onEditExpense != null && row.getItem() != null) {
                    onEditExpense.accept(row.getItem());
                }
            });

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(e -> {
                if (onDeleteExpense != null && row.getItem() != null) {
                    onDeleteExpense.accept(row.getItem().getId().toString());
                }
            });

            contextMenu.getItems().addAll(editItem, deleteItem);
            row.setContextMenu(contextMenu);
            return row;
        });
    }

    private void setupCategoryTable() {
        TableColumn<ExpenseCategory, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);

        TableColumn<ExpenseCategory, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(200);

        TableColumn<ExpenseCategory, String> colorCol = new TableColumn<>("Color");
        colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
        colorCol.setPrefWidth(80);
        colorCol.setCellFactory(col -> new TableCell<ExpenseCategory, String>() {
            @Override
            protected void updateItem(String color, boolean empty) {
                super.updateItem(color, empty);
                if (empty || color == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(color);
                    setStyle("-fx-background-color: " + color + ";");
                }
            }
        });

        categoryTable.getColumns().addAll(nameCol, descCol, colorCol);
        categoryTable.setRowFactory(tv -> {
            TableRow<ExpenseCategory> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(e -> {
                if (onEditCategory != null && row.getItem() != null) {
                    onEditCategory.accept(row.getItem());
                }
            });

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(e -> {
                if (onDeleteCategory != null && row.getItem() != null) {
                    onDeleteCategory.accept(row.getItem().getId().toString());
                }
            });

            contextMenu.getItems().addAll(editItem, deleteItem);
            row.setContextMenu(contextMenu);
            return row;
        });
    }

    private void setupBudgetTable() {
        TableColumn<BudgetLimit, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCategory() != null ?
                cellData.getValue().getCategory().getName() : ""));
        categoryCol.setPrefWidth(150);

        TableColumn<BudgetLimit, BigDecimal> limitCol = new TableColumn<>("Budget Limit");
        limitCol.setCellValueFactory(new PropertyValueFactory<>("limitAmount"));
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
        thresholdCol.setCellValueFactory(new PropertyValueFactory<>("alertThreshold"));
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

        budgetTable.getColumns().addAll(categoryCol, limitCol, thresholdCol);
        budgetTable.setRowFactory(tv -> {
            TableRow<BudgetLimit> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(e -> {
                if (onEditBudget != null && row.getItem() != null) {
                    onEditBudget.accept(row.getItem());
                }
            });

            MenuItem deleteItem = new MenuItem("Delete");
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
        nameCol.setCellValueFactory(new PropertyValueFactory<>("goalName"));
        nameCol.setPrefWidth(150);

        TableColumn<FinancialGoal, BigDecimal> targetCol = new TableColumn<>("Target");
        targetCol.setCellValueFactory(new PropertyValueFactory<>("targetAmount"));
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
        currentCol.setCellValueFactory(new PropertyValueFactory<>("currentAmount"));
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

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(e -> {
                if (onEditGoal != null && row.getItem() != null) {
                    onEditGoal.accept(row.getItem());
                }
            });

            MenuItem deleteItem = new MenuItem("Delete");
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

        spendingTrendChart = new LineChart<>(xAxis, yAxis);
        spendingTrendChart.setTitle("Spending Trends");
        spendingTrendChart.setPrefSize(600, 300);
    }

    private void setupLayout() {
        setSpacing(10);
        setPadding(new Insets(20));

        // Header with title and date selection
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(
            monthYearLabel,
            new Label("Month:"), monthComboBox,
            new Label("Year:"), yearComboBox
        );

        // Summary section
        HBox summaryBox = new HBox(30);
        summaryBox.setAlignment(Pos.CENTER);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        VBox spentBox = new VBox(5);
        spentBox.setAlignment(Pos.CENTER);
        spentBox.getChildren().addAll(new Label("Total Spent"), totalSpentLabel);

        VBox remainingBox = new VBox(5);
        remainingBox.setAlignment(Pos.CENTER);
        remainingBox.getChildren().addAll(new Label("Budget Remaining"), budgetRemainingLabel);

        summaryBox.getChildren().addAll(spentBox, remainingBox);

        // Action buttons
        HBox buttonBox = createActionButtons();

        // Alerts section
        VBox alertsSection = new VBox(5);
        alertsSection.getChildren().addAll(
            new Label("Budget Alerts"),
            alertsListView
        );

        // Charts section
        HBox chartsBox = new HBox(20);
        chartsBox.getChildren().addAll(categoryPieChart, spendingTrendChart);

        // Tables in tabs
        TabPane tabPane = new TabPane();

        Tab expensesTab = new Tab("Expenses");
        expensesTab.setClosable(false);
        expensesTab.setContent(new VBox(10, createExpenseButtons(), expenseTable));

        Tab categoriesTab = new Tab("Categories");
        categoriesTab.setClosable(false);
        categoriesTab.setContent(new VBox(10, createCategoryButtons(), categoryTable));

        Tab budgetsTab = new Tab("Budgets");
        budgetsTab.setClosable(false);
        budgetsTab.setContent(new VBox(10, createBudgetButtons(), budgetTable));

        Tab goalsTab = new Tab("Goals");
        goalsTab.setClosable(false);
        goalsTab.setContent(new VBox(10, createGoalButtons(), goalTable));

        tabPane.getTabs().addAll(expensesTab, categoriesTab, budgetsTab, goalsTab);

        getChildren().addAll(header, summaryBox, alertsSection, chartsBox, tabPane);
    }

    private HBox createActionButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button refreshButton = new Button("Refresh Data");
        refreshButton.setOnAction(e -> {
            if (onRefreshData != null) {
                onRefreshData.run();
            }
        });

        buttonBox.getChildren().add(refreshButton);
        return buttonBox;
    }

    private HBox createExpenseButtons() {
        HBox buttonBox = new HBox(10);

        Button addButton = new Button("Add Expense");
        addButton.setOnAction(e -> {
            if (onAddExpense != null) {
                onAddExpense.accept(new Expense());
            }
        });

        buttonBox.getChildren().add(addButton);
        return buttonBox;
    }

    private HBox createCategoryButtons() {
        HBox buttonBox = new HBox(10);

        Button addButton = new Button("Add Category");
        addButton.setOnAction(e -> {
            if (onAddCategory != null) {
                onAddCategory.accept(new ExpenseCategory());
            }
        });

        buttonBox.getChildren().add(addButton);
        return buttonBox;
    }

    private HBox createBudgetButtons() {
        HBox buttonBox = new HBox(10);

        Button addButton = new Button("Add Budget");
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

        Button addButton = new Button("Add Goal");
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
    public void updateExpenses(List<Expense> expenses) {
        expenseTable.setItems(FXCollections.observableArrayList(expenses));
    }

    public void updateCategories(List<ExpenseCategory> categories) {
        categoryTable.setItems(FXCollections.observableArrayList(categories));
    }

    public void updateBudgetLimits(List<BudgetLimit> budgets) {
        budgetTable.setItems(FXCollections.observableArrayList(budgets));
    }

    public void updateFinancialGoals(List<FinancialGoal> goals) {
        goalTable.setItems(FXCollections.observableArrayList(goals));
    }

    public void updateCategoryChart(Map<String, BigDecimal> categoryData) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        categoryData.forEach((category, amount) ->
            pieChartData.add(new PieChart.Data(category, amount.doubleValue())));
        categoryPieChart.setData(pieChartData);
    }

    public void updateSpendingTrendChart(List<Map<String, Object>> monthlyData) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Spending");

        for (Map<String, Object> data : monthlyData) {
            String month = (String) data.get("month");
            BigDecimal total = (BigDecimal) data.get("total");
            series.getData().add(new XYChart.Data<>(month, total.doubleValue()));
        }

        spendingTrendChart.getData().clear();
        spendingTrendChart.getData().add(series);
    }

    public void updateSummary(BigDecimal totalSpent, BigDecimal budgetRemaining) {
        totalSpentLabel.setText("$" + totalSpent.toString());
        budgetRemainingLabel.setText("$" + budgetRemaining.toString());

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
    public void setOnAddExpense(Consumer<Expense> onAddExpense) { this.onAddExpense = onAddExpense; }
    public void setOnEditExpense(Consumer<Expense> onEditExpense) { this.onEditExpense = onEditExpense; }
    public void setOnDeleteExpense(Consumer<String> onDeleteExpense) { this.onDeleteExpense = onDeleteExpense; }
    public void setOnAddCategory(Consumer<ExpenseCategory> onAddCategory) { this.onAddCategory = onAddCategory; }
    public void setOnEditCategory(Consumer<ExpenseCategory> onEditCategory) { this.onEditCategory = onEditCategory; }
    public void setOnDeleteCategory(Consumer<String> onDeleteCategory) { this.onDeleteCategory = onDeleteCategory; }
    public void setOnAddBudget(Consumer<BudgetLimit> onAddBudget) { this.onAddBudget = onAddBudget; }
    public void setOnEditBudget(Consumer<BudgetLimit> onEditBudget) { this.onEditBudget = onEditBudget; }
    public void setOnDeleteBudget(Consumer<String> onDeleteBudget) { this.onDeleteBudget = onDeleteBudget; }
    public void setOnAddGoal(Consumer<FinancialGoal> onAddGoal) { this.onAddGoal = onAddGoal; }
    public void setOnEditGoal(Consumer<FinancialGoal> onEditGoal) { this.onEditGoal = onEditGoal; }
    public void setOnDeleteGoal(Consumer<String> onDeleteGoal) { this.onDeleteGoal = onDeleteGoal; }
    public void setOnRefreshData(Runnable onRefreshData) { this.onRefreshData = onRefreshData; }
}
