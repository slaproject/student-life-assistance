package com.studentapp.frontend.view;

import com.studentapp.common.model.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.util.function.Consumer;

/**
 * Expense Tracker View focused on expense entry, editing, and category management.
 * This is a streamlined interface for day-to-day expense tracking.
 */
public class ExpenseTrackerView extends VBox {

    // UI Components
    private TableView<Expense> expenseTable;
    private TableView<ExpenseCategory> categoryTable;
    private Label totalExpensesLabel;
    private Label monthYearLabel;
    private ComboBox<String> monthComboBox;
    private ComboBox<Integer> yearComboBox;
    private ComboBox<ExpenseCategory> filterCategoryComboBox;
    private TextField searchField;

    // Action handlers
    private Consumer<Expense> onAddExpense;
    private Consumer<Expense> onEditExpense;
    private Consumer<String> onDeleteExpense;
    private Consumer<ExpenseCategory> onAddCategory;
    private Consumer<ExpenseCategory> onEditCategory;
    private Consumer<String> onDeleteCategory;
    private Runnable onRefreshData;

    public ExpenseTrackerView() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        // Month/Year selection
        monthComboBox = new ComboBox<>();
        monthComboBox.getItems().addAll(
            "All", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        );
        monthComboBox.setValue("All");

        yearComboBox = new ComboBox<>();
        int currentYear = LocalDate.now().getYear();
        yearComboBox.getItems().add(0); // Represents "All years"
        for (int i = currentYear - 5; i <= currentYear + 2; i++) {
            yearComboBox.getItems().add(i);
        }
        yearComboBox.setValue(currentYear);

        // Filter and search
        filterCategoryComboBox = new ComboBox<>();
        filterCategoryComboBox.setPromptText("Filter by category");

        searchField = new TextField();
        searchField.setPromptText("Search expenses...");
        searchField.setPrefWidth(200);

        // Summary label
        totalExpensesLabel = new Label("Total: $0.00");
        totalExpensesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalExpensesLabel.setTextFill(Color.DARKBLUE);

        monthYearLabel = new Label("Expense Tracker");
        monthYearLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Expense Table
        expenseTable = new TableView<>();
        setupExpenseTable();

        // Category Table
        categoryTable = new TableView<>();
        setupCategoryTable();
    }

    private void setupExpenseTable() {
        TableColumn<Expense, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(180);

        TableColumn<Expense, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCategory() != null ?
                cellData.getValue().getCategory().getName() : "Uncategorized"));
        categoryCol.setPrefWidth(120);

        TableColumn<Expense, BigDecimal> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(100);
        amountCol.setCellFactory(col -> new TableCell<Expense, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText("$" + String.format("%.2f", amount.doubleValue()));
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

        TableColumn<Expense, String> paymentCol = new TableColumn<>("Payment Method");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentCol.setPrefWidth(120);

        TableColumn<Expense, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setPrefWidth(150);

        expenseTable.getColumns().addAll(titleCol, categoryCol, amountCol, dateCol, paymentCol, descriptionCol);
        expenseTable.setRowFactory(tv -> {
            TableRow<Expense> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit Expense");
            editItem.setOnAction(e -> {
                if (onEditExpense != null && row.getItem() != null) {
                    onEditExpense.accept(row.getItem());
                }
            });

            MenuItem deleteItem = new MenuItem("Delete Expense");
            deleteItem.setOnAction(e -> {
                if (onDeleteExpense != null && row.getItem() != null) {
                    onDeleteExpense.accept(row.getItem().getId().toString());
                }
            });

            MenuItem duplicateItem = new MenuItem("Duplicate Expense");
            duplicateItem.setOnAction(e -> {
                if (onAddExpense != null && row.getItem() != null) {
                    Expense duplicate = duplicateExpense(row.getItem());
                    onAddExpense.accept(duplicate);
                }
            });

            contextMenu.getItems().addAll(editItem, duplicateItem, new SeparatorMenuItem(), deleteItem);
            row.setContextMenu(contextMenu);
            return row;
        });

        // Allow multiple selection for bulk operations
        expenseTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void setupCategoryTable() {
        TableColumn<ExpenseCategory, String> nameCol = new TableColumn<>("Category Name");
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
                    setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<ExpenseCategory, String> iconCol = new TableColumn<>("Icon");
        iconCol.setCellValueFactory(new PropertyValueFactory<>("icon"));
        iconCol.setPrefWidth(80);

        TableColumn<ExpenseCategory, Boolean> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("isActive"));
        activeCol.setPrefWidth(80);
        activeCol.setCellFactory(col -> new TableCell<ExpenseCategory, Boolean>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                if (empty || active == null) {
                    setText(null);
                } else {
                    setText(active ? "✓" : "✗");
                    setTextFill(active ? Color.GREEN : Color.RED);
                }
            }
        });

        categoryTable.getColumns().addAll(nameCol, descCol, colorCol, iconCol, activeCol);
        categoryTable.setRowFactory(tv -> {
            TableRow<ExpenseCategory> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit Category");
            editItem.setOnAction(e -> {
                if (onEditCategory != null && row.getItem() != null) {
                    onEditCategory.accept(row.getItem());
                }
            });

            MenuItem toggleItem = new MenuItem("Toggle Active/Inactive");
            toggleItem.setOnAction(e -> {
                if (row.getItem() != null) {
                    ExpenseCategory category = row.getItem();
                    category.setIsActive(!category.getIsActive());
                    if (onEditCategory != null) {
                        onEditCategory.accept(category);
                    }
                }
            });

            MenuItem deleteItem = new MenuItem("Delete Category");
            deleteItem.setOnAction(e -> {
                if (onDeleteCategory != null && row.getItem() != null) {
                    onDeleteCategory.accept(row.getItem().getId().toString());
                }
            });

            contextMenu.getItems().addAll(editItem, toggleItem, new SeparatorMenuItem(), deleteItem);
            row.setContextMenu(contextMenu);
            return row;
        });
    }

    private void setupLayout() {
        setSpacing(15);
        setPadding(new Insets(20));

        // Header with title and filters
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(
            monthYearLabel,
            new Label("Month:"), monthComboBox,
            new Label("Year:"), yearComboBox,
            new Label("Category:"), filterCategoryComboBox,
            searchField
        );

        // Summary section
        HBox summaryBox = new HBox(20);
        summaryBox.setAlignment(Pos.CENTER);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 5;");
        summaryBox.getChildren().addAll(totalExpensesLabel);

        // Tables in tabs
        TabPane tabPane = new TabPane();

        Tab expensesTab = new Tab("Expenses");
        expensesTab.setClosable(false);
        VBox expenseContent = new VBox(10);
        expenseContent.getChildren().addAll(
            createExpenseButtons(),
            createBulkOperationsBox(),
            expenseTable
        );
        expensesTab.setContent(expenseContent);

        Tab categoriesTab = new Tab("Categories");
        categoriesTab.setClosable(false);
        categoriesTab.setContent(new VBox(10, createCategoryButtons(), categoryTable));

        tabPane.getTabs().addAll(expensesTab, categoriesTab);

        getChildren().addAll(header, summaryBox, tabPane);
    }

    private HBox createExpenseButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button addButton = new Button("Add Expense");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> {
            if (onAddExpense != null) {
                onAddExpense.accept(new Expense());
            }
        });

        Button quickAddButton = new Button("Quick Add");
        quickAddButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
        quickAddButton.setOnAction(e -> showQuickAddDialog());

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            if (onRefreshData != null) {
                onRefreshData.run();
            }
        });

        buttonBox.getChildren().addAll(addButton, quickAddButton, refreshButton);
        return buttonBox;
    }

    private HBox createBulkOperationsBox() {
        HBox bulkBox = new HBox(10);
        bulkBox.setAlignment(Pos.CENTER_LEFT);

        Label bulkLabel = new Label("Bulk Operations:");
        bulkLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Button deleteSelectedButton = new Button("Delete Selected");
        deleteSelectedButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        deleteSelectedButton.setOnAction(e -> deleteSelectedExpenses());

        Button exportButton = new Button("Export to CSV");
        exportButton.setOnAction(e -> exportExpenses());

        bulkBox.getChildren().addAll(bulkLabel, deleteSelectedButton, exportButton);
        return bulkBox;
    }

    private HBox createCategoryButtons() {
        HBox buttonBox = new HBox(10);

        Button addButton = new Button("Add Category");
        addButton.setStyle("-fx-background-color: #fd7e14; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> {
            if (onAddCategory != null) {
                onAddCategory.accept(new ExpenseCategory());
            }
        });

        Button importButton = new Button("Import Categories");
        importButton.setOnAction(e -> importCategories());

        buttonBox.getChildren().addAll(addButton, importButton);
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

        filterCategoryComboBox.setOnAction(e -> {
            if (onRefreshData != null) {
                onRefreshData.run();
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Implement search filtering
            filterExpenseTable(newValue);
        });
    }

    // Helper methods
    private Expense duplicateExpense(Expense original) {
        Expense duplicate = new Expense();
        duplicate.setTitle(original.getTitle() + " (Copy)");
        duplicate.setDescription(original.getDescription());
        duplicate.setAmount(original.getAmount());
        duplicate.setExpenseDate(LocalDate.now()); // Set to today's date
        duplicate.setCategory(original.getCategory());
        duplicate.setPaymentMethod(original.getPaymentMethod());
        duplicate.setLocation(original.getLocation());
        duplicate.setTags(original.getTags());
        return duplicate;
    }

    private void showQuickAddDialog() {
        // Simple quick add dialog implementation
        Dialog<Expense> dialog = new Dialog<>();
        dialog.setTitle("Quick Add Expense");
        dialog.setHeaderText("Add a quick expense");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Expense title");
        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    Expense expense = new Expense();
                    expense.setTitle(titleField.getText());
                    expense.setAmount(new BigDecimal(amountField.getText()));
                    expense.setExpenseDate(LocalDate.now());
                    return expense;
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(expense -> {
            if (onAddExpense != null) {
                onAddExpense.accept(expense);
            }
        });
    }

    private void deleteSelectedExpenses() {
        List<Expense> selected = expenseTable.getSelectionModel().getSelectedItems();
        if (!selected.isEmpty() && onDeleteExpense != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Expenses");
            confirm.setHeaderText("Delete " + selected.size() + " expense(s)?");
            confirm.setContentText("This action cannot be undone.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    for (Expense expense : selected) {
                        onDeleteExpense.accept(expense.getId().toString());
                    }
                }
            });
        }
    }

    private void exportExpenses() {
        // TODO: Implement CSV export functionality
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Export");
        info.setHeaderText("Export functionality");
        info.setContentText("CSV export feature will be implemented soon.");
        info.showAndWait();
    }

    private void importCategories() {
        // TODO: Implement category import functionality
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Import");
        info.setHeaderText("Import functionality");
        info.setContentText("Category import feature will be implemented soon.");
        info.showAndWait();
    }

    private void filterExpenseTable(String searchText) {
        // TODO: Implement table filtering based on search text
        // This would filter the observable list in the table
    }

    // Public methods for updating data
    public void updateExpenses(List<Expense> expenses) {
        expenseTable.setItems(FXCollections.observableArrayList(expenses));

        // Update total
        BigDecimal total = expenses.stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalExpensesLabel.setText("Total: $" + String.format("%.2f", total.doubleValue()));
    }

    public void updateCategories(List<ExpenseCategory> categories) {
        categoryTable.setItems(FXCollections.observableArrayList(categories));

        // Update filter dropdown
        filterCategoryComboBox.getItems().clear();
        filterCategoryComboBox.getItems().add(null); // "All categories" option
        filterCategoryComboBox.getItems().addAll(categories);
    }

    public int getSelectedMonth() {
        int index = monthComboBox.getSelectionModel().getSelectedIndex();
        return index == 0 ? -1 : index; // -1 for "All"
    }

    public int getSelectedYear() {
        return yearComboBox.getValue() == 0 ? -1 : yearComboBox.getValue(); // -1 for "All"
    }

    public ExpenseCategory getSelectedCategoryFilter() {
        return filterCategoryComboBox.getValue();
    }

    public String getSearchText() {
        return searchField.getText();
    }

    // Setters for action handlers
    public void setOnAddExpense(Consumer<Expense> onAddExpense) { this.onAddExpense = onAddExpense; }
    public void setOnEditExpense(Consumer<Expense> onEditExpense) { this.onEditExpense = onEditExpense; }
    public void setOnDeleteExpense(Consumer<String> onDeleteExpense) { this.onDeleteExpense = onDeleteExpense; }
    public void setOnAddCategory(Consumer<ExpenseCategory> onAddCategory) { this.onAddCategory = onAddCategory; }
    public void setOnEditCategory(Consumer<ExpenseCategory> onEditCategory) { this.onEditCategory = onEditCategory; }
    public void setOnDeleteCategory(Consumer<String> onDeleteCategory) { this.onDeleteCategory = onDeleteCategory; }
    public void setOnRefreshData(Runnable onRefreshData) { this.onRefreshData = onRefreshData; }
}
