package com.studentapp.frontend.controller;

import com.studentapp.common.model.*;
import com.studentapp.frontend.view.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for the Finance module that manages the interaction between
 * the FinanceView and the backend services through API clients.
 */
public class FinanceController {

    private FinanceView financeView;
    // TODO: Add API client when available
    // private FinanceApiClient apiClient;

    private List<ExpenseCategory> categories = new ArrayList<>();
    private List<Expense> expenses = new ArrayList<>();
    private List<BudgetLimit> budgetLimits = new ArrayList<>();
    private List<FinancialGoal> financialGoals = new ArrayList<>();

    public FinanceController() {
        initializeView();
        setupEventHandlers();
        loadInitialData();
    }

    private void initializeView() {
        financeView = new FinanceView();
    }

    private void setupEventHandlers() {
        // Expense handlers
        financeView.setOnAddExpense(this::showAddExpenseDialog);
        financeView.setOnEditExpense(this::showEditExpenseDialog);
        financeView.setOnDeleteExpense(this::deleteExpense);

        // Category handlers
        financeView.setOnAddCategory(this::showAddCategoryDialog);
        financeView.setOnEditCategory(this::showEditCategoryDialog);
        financeView.setOnDeleteCategory(this::deleteCategory);

        // Budget handlers
        financeView.setOnAddBudget(this::showAddBudgetDialog);
        financeView.setOnEditBudget(this::showEditBudgetDialog);
        financeView.setOnDeleteBudget(this::deleteBudgetLimit);

        // Goal handlers
        financeView.setOnAddGoal(this::showAddGoalDialog);
        financeView.setOnEditGoal(this::showEditGoalDialog);
        financeView.setOnDeleteGoal(this::deleteFinancialGoal);

        // Refresh handler
        financeView.setOnRefreshData(this::refreshData);
    }

    private void loadInitialData() {
        // Create some sample data for demonstration
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

        categories.addAll(Arrays.asList(foodCategory, transportCategory));

        // Sample expenses
        Expense expense1 = new Expense();
        expense1.setId(UUID.randomUUID());
        expense1.setTitle("Lunch at Campus Cafeteria");
        expense1.setAmount(new BigDecimal("12.50"));
        expense1.setExpenseDate(java.time.LocalDate.now());
        expense1.setCategory(foodCategory);
        expense1.setPaymentMethod("card");

        Expense expense2 = new Expense();
        expense2.setId(UUID.randomUUID());
        expense2.setTitle("Bus Pass");
        expense2.setAmount(new BigDecimal("85.00"));
        expense2.setExpenseDate(java.time.LocalDate.now().minusDays(2));
        expense2.setCategory(transportCategory);
        expense2.setPaymentMethod("cash");

        expenses.addAll(Arrays.asList(expense1, expense2));

        // Sample budget limits
        BudgetLimit foodBudget = new BudgetLimit();
        foodBudget.setId(UUID.randomUUID());
        foodBudget.setCategory(foodCategory);
        foodBudget.setBudgetMonth(java.time.LocalDate.now().getMonthValue());
        foodBudget.setBudgetYear(java.time.LocalDate.now().getYear());
        foodBudget.setLimitAmount(new BigDecimal("200.00"));
        foodBudget.setAlertThreshold(new BigDecimal("75.0"));

        budgetLimits.add(foodBudget);

        // Sample financial goal
        FinancialGoal emergencyFund = new FinancialGoal();
        emergencyFund.setId(UUID.randomUUID());
        emergencyFund.setGoalName("Emergency Fund");
        emergencyFund.setDescription("Save for unexpected expenses");
        emergencyFund.setTargetAmount(new BigDecimal("1000.00"));
        emergencyFund.setCurrentAmount(new BigDecimal("250.00"));
        emergencyFund.setTargetDate(java.time.LocalDate.now().plusMonths(6));
        emergencyFund.setGoalType("savings");
        emergencyFund.setIsActive(true);

        financialGoals.add(emergencyFund);
    }

    private void refreshData() {
        // Update view with current data
        financeView.updateExpenses(expenses);
        financeView.updateCategories(categories);
        financeView.updateBudgetLimits(budgetLimits);
        financeView.updateFinancialGoals(financialGoals);

        // Update analytics
        updateAnalytics();
    }

    private void updateAnalytics() {
        int month = financeView.getSelectedMonth();
        int year = financeView.getSelectedYear();

        // Calculate monthly totals
        BigDecimal totalSpent = expenses.stream()
            .filter(e -> e.getExpenseDate().getMonthValue() == month &&
                        e.getExpenseDate().getYear() == year)
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate budget remaining
        BigDecimal totalBudget = budgetLimits.stream()
            .filter(b -> b.getBudgetMonth() == month && b.getBudgetYear() == year)
            .map(BudgetLimit::getLimitAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remaining = totalBudget.subtract(totalSpent);

        financeView.updateSummary(totalSpent, remaining);

        // Update category chart
        Map<String, BigDecimal> categoryData = new HashMap<>();
        expenses.stream()
            .filter(e -> e.getExpenseDate().getMonthValue() == month &&
                        e.getExpenseDate().getYear() == year)
            .forEach(expense -> {
                String categoryName = expense.getCategory() != null ?
                    expense.getCategory().getName() : "Uncategorized";
                categoryData.merge(categoryName, expense.getAmount(), BigDecimal::add);
            });

        financeView.updateCategoryChart(categoryData);

        // Generate sample spending trend data
        List<Map<String, Object>> trendData = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            Map<String, Object> monthData = new HashMap<>();
            java.time.LocalDate date = java.time.LocalDate.now().minusMonths(i);
            monthData.put("month", date.getMonth().name().substring(0, 3) + " " + date.getYear());
            monthData.put("total", new BigDecimal(Math.random() * 300 + 100)); // Sample data
            trendData.add(monthData);
        }

        financeView.updateSpendingTrendChart(trendData);

        // Update alerts
        List<String> alerts = new ArrayList<>();
        budgetLimits.stream()
            .filter(b -> b.getBudgetMonth() == month && b.getBudgetYear() == year)
            .forEach(budget -> {
                BigDecimal spent = expenses.stream()
                    .filter(e -> e.getCategory().equals(budget.getCategory()) &&
                                e.getExpenseDate().getMonthValue() == month &&
                                e.getExpenseDate().getYear() == year)
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (budget.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal percentage = spent.multiply(BigDecimal.valueOf(100))
                        .divide(budget.getLimitAmount(), 2, BigDecimal.ROUND_HALF_UP);

                    if (percentage.compareTo(budget.getAlertThreshold()) >= 0) {
                        alerts.add(String.format("%s: %.1f%% of budget used ($%.2f / $%.2f)",
                            budget.getCategory().getName(),
                            percentage.doubleValue(),
                            spent.doubleValue(),
                            budget.getLimitAmount().doubleValue()));
                    }
                }
            });

        financeView.updateAlerts(alerts);
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
            refreshData();
            showSuccessAlert("Category updated successfully!");
        });
    }

    private void deleteCategory(String categoryId) {
        if (showConfirmationAlert("Delete Category", "Are you sure you want to delete this category?")) {
            categories.removeIf(category -> category.getId().toString().equals(categoryId));
            refreshData();
            showSuccessAlert("Category deleted successfully!");
        }
    }

    // Budget dialog handlers
    private void showAddBudgetDialog(BudgetLimit budgetLimit) {
        BudgetDialog dialog = new BudgetDialog(budgetLimit, categories);
        Optional<BudgetLimit> result = dialog.showAndWait();

        result.ifPresent(newBudget -> {
            newBudget.setId(UUID.randomUUID());
            budgetLimits.add(newBudget);
            refreshData();
            showSuccessAlert("Budget limit added successfully!");
        });
    }

    private void showEditBudgetDialog(BudgetLimit budgetLimit) {
        BudgetDialog dialog = new BudgetDialog(budgetLimit, categories);
        Optional<BudgetLimit> result = dialog.showAndWait();

        result.ifPresent(updatedBudget -> {
            // Update the budget in the list
            for (int i = 0; i < budgetLimits.size(); i++) {
                if (budgetLimits.get(i).getId().equals(budgetLimit.getId())) {
                    budgetLimits.set(i, updatedBudget);
                    break;
                }
            }
            refreshData();
            showSuccessAlert("Budget limit updated successfully!");
        });
    }

    private void deleteBudgetLimit(String budgetId) {
        if (showConfirmationAlert("Delete Budget Limit", "Are you sure you want to delete this budget limit?")) {
            budgetLimits.removeIf(budget -> budget.getId().toString().equals(budgetId));
            refreshData();
            showSuccessAlert("Budget limit deleted successfully!");
        }
    }

    // Goal dialog handlers
    private void showAddGoalDialog(FinancialGoal goal) {
        FinancialGoalDialog dialog = new FinancialGoalDialog(goal);
        Optional<FinancialGoal> result = dialog.showAndWait();

        result.ifPresent(newGoal -> {
            newGoal.setId(UUID.randomUUID());
            financialGoals.add(newGoal);
            refreshData();
            showSuccessAlert("Financial goal added successfully!");
        });
    }

    private void showEditGoalDialog(FinancialGoal goal) {
        FinancialGoalDialog dialog = new FinancialGoalDialog(goal);
        Optional<FinancialGoal> result = dialog.showAndWait();

        result.ifPresent(updatedGoal -> {
            // Update the goal in the list
            for (int i = 0; i < financialGoals.size(); i++) {
                if (financialGoals.get(i).getId().equals(goal.getId())) {
                    financialGoals.set(i, updatedGoal);
                    break;
                }
            }
            refreshData();
            showSuccessAlert("Financial goal updated successfully!");
        });
    }

    private void deleteFinancialGoal(String goalId) {
        if (showConfirmationAlert("Delete Financial Goal", "Are you sure you want to delete this financial goal?")) {
            financialGoals.removeIf(goal -> goal.getId().toString().equals(goalId));
            refreshData();
            showSuccessAlert("Financial goal deleted successfully!");
        }
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

    public FinanceView getView() {
        return financeView;
    }
}
