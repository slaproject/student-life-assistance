package com.studentapp.frontend.controller;

import com.studentapp.common.model.*;
import com.studentapp.frontend.view.ExpenseTrackerView;
import com.studentapp.frontend.view.ExpenseDialog;
import com.studentapp.frontend.view.CategoryDialog;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the Expense Tracker that manages daily expense entry,
 * editing, and category management.
 */
public class ExpenseTrackerController {

    private ExpenseTrackerView expenseView;
    // TODO: Add API client when available
    // private FinanceApiClient apiClient;

    private List<ExpenseCategory> categories = new ArrayList<>();
    private List<Expense> expenses = new ArrayList<>();
    private List<Expense> filteredExpenses = new ArrayList<>();

    public ExpenseTrackerController() {
        initializeView();
        setupEventHandlers();
        loadInitialData();
    }

    private void initializeView() {
        expenseView = new ExpenseTrackerView();
    }

    private void setupEventHandlers() {
        // Expense handlers
        expenseView.setOnAddExpense(this::showAddExpenseDialog);
        expenseView.setOnEditExpense(this::showEditExpenseDialog);
        expenseView.setOnDeleteExpense(this::deleteExpense);

        // Category handlers
        expenseView.setOnAddCategory(this::showAddCategoryDialog);
        expenseView.setOnEditCategory(this::showEditCategoryDialog);
        expenseView.setOnDeleteCategory(this::deleteCategory);

        // Refresh handler
        expenseView.setOnRefreshData(this::refreshData);
    }

    private void loadInitialData() {
        createSampleData();
        refreshData();
    }

    private void createSampleData() {
        // Sample categories
        ExpenseCategory foodCategory = new ExpenseCategory();
        foodCategory.setId(UUID.randomUUID());
        foodCategory.setName("Food & Dining");
        foodCategory.setDescription("Restaurants, groceries, takeout");
        foodCategory.setColor("#FF6B6B");
        foodCategory.setIcon("restaurant");
        foodCategory.setIsActive(true);

        ExpenseCategory transportCategory = new ExpenseCategory();
        transportCategory.setId(UUID.randomUUID());
        transportCategory.setName("Transportation");
        transportCategory.setDescription("Gas, public transport, parking");
        transportCategory.setColor("#4ECDC4");
        transportCategory.setIcon("car");
        transportCategory.setIsActive(true);

        ExpenseCategory entertainmentCategory = new ExpenseCategory();
        entertainmentCategory.setId(UUID.randomUUID());
        entertainmentCategory.setName("Entertainment");
        entertainmentCategory.setDescription("Movies, games, subscriptions");
        entertainmentCategory.setColor("#45B7D1");
        entertainmentCategory.setIcon("movie");
        entertainmentCategory.setIsActive(true);

        ExpenseCategory educationCategory = new ExpenseCategory();
        educationCategory.setId(UUID.randomUUID());
        educationCategory.setName("Education");
        educationCategory.setDescription("Books, courses, supplies");
        educationCategory.setColor("#96CEB4");
        educationCategory.setIcon("book");
        educationCategory.setIsActive(true);

        categories.addAll(Arrays.asList(foodCategory, transportCategory, entertainmentCategory, educationCategory));

        // Sample expenses with more variety
        expenses.addAll(Arrays.asList(
            createSampleExpense("Lunch at Campus Cafeteria", new BigDecimal("12.50"), LocalDate.now(), foodCategory, "card", "Daily lunch meal"),
            createSampleExpense("Bus Pass", new BigDecimal("85.00"), LocalDate.now().minusDays(2), transportCategory, "cash", "Monthly transportation pass"),
            createSampleExpense("Netflix Subscription", new BigDecimal("15.99"), LocalDate.now().minusDays(5), entertainmentCategory, "card", "Monthly streaming service"),
            createSampleExpense("Coffee & Study Session", new BigDecimal("4.75"), LocalDate.now().minusDays(1), foodCategory, "digital_wallet", "Study fuel"),
            createSampleExpense("Textbook - Programming", new BigDecimal("89.99"), LocalDate.now().minusDays(7), educationCategory, "card", "Required textbook for CS class"),
            createSampleExpense("Grocery Shopping", new BigDecimal("45.30"), LocalDate.now().minusDays(3), foodCategory, "card", "Weekly groceries"),
            createSampleExpense("Movie Tickets", new BigDecimal("24.00"), LocalDate.now().minusDays(4), entertainmentCategory, "cash", "Weekend entertainment"),
            createSampleExpense("Gas", new BigDecimal("35.50"), LocalDate.now().minusDays(6), transportCategory, "card", "Fuel for car")
        ));
    }

    private Expense createSampleExpense(String title, BigDecimal amount, LocalDate date, ExpenseCategory category, String paymentMethod, String description) {
        Expense expense = new Expense();
        expense.setId(UUID.randomUUID());
        expense.setTitle(title);
        expense.setAmount(amount);
        expense.setExpenseDate(date);
        expense.setCategory(category);
        expense.setPaymentMethod(paymentMethod);
        expense.setDescription(description);
        return expense;
    }

    private void refreshData() {
        // Apply filters
        filterExpenses();

        // Update views
        expenseView.updateExpenses(filteredExpenses);
        expenseView.updateCategories(categories);
    }

    private void filterExpenses() {
        filteredExpenses = new ArrayList<>(expenses);

        // Filter by month
        int selectedMonth = expenseView.getSelectedMonth();
        if (selectedMonth != -1) { // -1 means "All"
            filteredExpenses = filteredExpenses.stream()
                .filter(e -> e.getExpenseDate().getMonthValue() == selectedMonth)
                .collect(Collectors.toList());
        }

        // Filter by year
        int selectedYear = expenseView.getSelectedYear();
        if (selectedYear != -1) { // -1 means "All"
            filteredExpenses = filteredExpenses.stream()
                .filter(e -> e.getExpenseDate().getYear() == selectedYear)
                .collect(Collectors.toList());
        }

        // Filter by category
        ExpenseCategory selectedCategory = expenseView.getSelectedCategoryFilter();
        if (selectedCategory != null) {
            filteredExpenses = filteredExpenses.stream()
                .filter(e -> e.getCategory() != null && e.getCategory().equals(selectedCategory))
                .collect(Collectors.toList());
        }

        // Filter by search text
        String searchText = expenseView.getSearchText();
        if (searchText != null && !searchText.trim().isEmpty()) {
            String searchLower = searchText.toLowerCase();
            filteredExpenses = filteredExpenses.stream()
                .filter(e ->
                    e.getTitle().toLowerCase().contains(searchLower) ||
                    (e.getDescription() != null && e.getDescription().toLowerCase().contains(searchLower)) ||
                    (e.getLocation() != null && e.getLocation().toLowerCase().contains(searchLower)) ||
                    (e.getTags() != null && e.getTags().toLowerCase().contains(searchLower)))
                .collect(Collectors.toList());
        }
    }

    // Expense dialog handlers
    private void showAddExpenseDialog(Expense expense) {
        ExpenseDialog dialog = new ExpenseDialog(expense, categories);
        Optional<Expense> result = dialog.showAndWait();

        result.ifPresent(newExpense -> {
            newExpense.setId(UUID.randomUUID());
            expenses.add(newExpense);
            refreshData();
            showSuccessAlert("Expense added successfully!");
        });
    }

    private void showEditExpenseDialog(Expense expense) {
        ExpenseDialog dialog = new ExpenseDialog(expense, categories);
        Optional<Expense> result = dialog.showAndWait();

        result.ifPresent(updatedExpense -> {
            // Update the expense in the list
            for (int i = 0; i < expenses.size(); i++) {
                if (expenses.get(i).getId().equals(expense.getId())) {
                    expenses.set(i, updatedExpense);
                    break;
                }
            }
            refreshData();
            showSuccessAlert("Expense updated successfully!");
        });
    }

    private void deleteExpense(String expenseId) {
        if (showConfirmationAlert("Delete Expense", "Are you sure you want to delete this expense?")) {
            expenses.removeIf(expense -> expense.getId().toString().equals(expenseId));
            refreshData();
            showSuccessAlert("Expense deleted successfully!");
        }
    }

    // Category dialog handlers
    private void showAddCategoryDialog(ExpenseCategory category) {
        CategoryDialog dialog = new CategoryDialog(category);
        Optional<ExpenseCategory> result = dialog.showAndWait();

        result.ifPresent(newCategory -> {
            newCategory.setId(UUID.randomUUID());
            categories.add(newCategory);
            refreshData();
            showSuccessAlert("Category added successfully!");
        });
    }

    private void showEditCategoryDialog(ExpenseCategory category) {
        CategoryDialog dialog = new CategoryDialog(category);
        Optional<ExpenseCategory> result = dialog.showAndWait();

        result.ifPresent(updatedCategory -> {
            // Update the category in the list
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId().equals(category.getId())) {
                    categories.set(i, updatedCategory);
                    break;
                }
            }

            // Update any expenses that use this category
            expenses.stream()
                .filter(expense -> expense.getCategory() != null &&
                                 expense.getCategory().getId().equals(category.getId()))
                .forEach(expense -> expense.setCategory(updatedCategory));

            refreshData();
            showSuccessAlert("Category updated successfully!");
        });
    }

    private void deleteCategory(String categoryId) {
        // Check if any expenses use this category
        long expenseCount = expenses.stream()
            .filter(expense -> expense.getCategory() != null &&
                             expense.getCategory().getId().toString().equals(categoryId))
            .count();

        if (expenseCount > 0) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Category In Use");
            warning.setHeaderText("Cannot delete category");
            warning.setContentText(String.format("This category is used by %d expense(s). " +
                "Please reassign or delete those expenses first.", expenseCount));
            warning.showAndWait();
            return;
        }

        if (showConfirmationAlert("Delete Category", "Are you sure you want to delete this category?")) {
            categories.removeIf(category -> category.getId().toString().equals(categoryId));
            refreshData();
            showSuccessAlert("Category deleted successfully!");
        }
    }

    // Statistics methods for expense analysis
    public Map<String, Object> getExpenseStatistics() {
        Map<String, Object> stats = new HashMap<>();

        if (filteredExpenses.isEmpty()) {
            return stats;
        }

        // Total expenses
        BigDecimal total = filteredExpenses.stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("total", total);

        // Average expense
        BigDecimal average = total.divide(BigDecimal.valueOf(filteredExpenses.size()), 2, BigDecimal.ROUND_HALF_UP);
        stats.put("average", average);

        // Largest expense
        Optional<Expense> largest = filteredExpenses.stream()
            .max(Comparator.comparing(Expense::getAmount));
        stats.put("largest", largest.orElse(null));

        // Most frequent category
        Map<String, Long> categoryFrequency = filteredExpenses.stream()
            .filter(e -> e.getCategory() != null)
            .collect(Collectors.groupingBy(e -> e.getCategory().getName(), Collectors.counting()));

        Optional<Map.Entry<String, Long>> mostFrequent = categoryFrequency.entrySet().stream()
            .max(Map.Entry.comparingByValue());
        stats.put("mostFrequentCategory", mostFrequent.map(Map.Entry::getKey).orElse("None"));

        // Category totals
        Map<String, BigDecimal> categoryTotals = filteredExpenses.stream()
            .filter(e -> e.getCategory() != null)
            .collect(Collectors.groupingBy(
                e -> e.getCategory().getName(),
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));
        stats.put("categoryTotals", categoryTotals);

        return stats;
    }

    // Export functionality
    public List<String> exportExpensesToCSV() {
        List<String> csvLines = new ArrayList<>();

        // Header
        csvLines.add("Date,Title,Category,Amount,Payment Method,Description,Location,Tags");

        // Data rows
        for (Expense expense : filteredExpenses) {
            StringBuilder line = new StringBuilder();
            line.append(expense.getExpenseDate() != null ? expense.getExpenseDate().toString() : "");
            line.append(",");
            line.append(escapeCSV(expense.getTitle()));
            line.append(",");
            line.append(expense.getCategory() != null ? escapeCSV(expense.getCategory().getName()) : "");
            line.append(",");
            line.append(expense.getAmount() != null ? expense.getAmount().toString() : "0.00");
            line.append(",");
            line.append(escapeCSV(expense.getPaymentMethod()));
            line.append(",");
            line.append(escapeCSV(expense.getDescription()));
            line.append(",");
            line.append(escapeCSV(expense.getLocation()));
            line.append(",");
            line.append(escapeCSV(expense.getTags()));

            csvLines.add(line.toString());
        }

        return csvLines;
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // Utility methods
    private void showSuccessAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private boolean showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // Getters
    public ExpenseTrackerView getView() {
        return expenseView;
    }

    public List<Expense> getFilteredExpenses() {
        return new ArrayList<>(filteredExpenses);
    }

    public List<ExpenseCategory> getCategories() {
        return new ArrayList<>(categories);
    }
}
