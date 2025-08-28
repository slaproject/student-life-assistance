package com.studentapp.frontend.controller;

import com.studentapp.common.model.*;
import com.studentapp.frontend.view.BudgetDashboardView;
import com.studentapp.frontend.view.BudgetDialog;
import com.studentapp.frontend.view.FinancialGoalDialog;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the Budget Dashboard that manages financial overview,
 * budget limits, goals, and spending analysis.
 */
public class BudgetDashboardController {

    private BudgetDashboardView budgetView;
    // TODO: Add API client when available
    // private FinanceApiClient apiClient;

    private List<ExpenseCategory> categories = new ArrayList<>();
    private List<Expense> expenses = new ArrayList<>();
    private List<BudgetLimit> budgetLimits = new ArrayList<>();
    private List<FinancialGoal> financialGoals = new ArrayList<>();

    public BudgetDashboardController() {
        initializeView();
        setupEventHandlers();
        loadInitialData();
    }

    private void initializeView() {
        budgetView = new BudgetDashboardView();
    }

    private void setupEventHandlers() {
        // Budget handlers
        budgetView.setOnAddBudget(this::showAddBudgetDialog);
        budgetView.setOnEditBudget(this::showEditBudgetDialog);
        budgetView.setOnDeleteBudget(this::deleteBudgetLimit);

        // Goal handlers
        budgetView.setOnAddGoal(this::showAddGoalDialog);
        budgetView.setOnEditGoal(this::showEditGoalDialog);
        budgetView.setOnDeleteGoal(this::deleteFinancialGoal);

        // Refresh handler
        budgetView.setOnRefreshData(this::refreshData);
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

        // Add Education category to align with sample data used elsewhere
        ExpenseCategory educationCategory = new ExpenseCategory();
        educationCategory.setId(UUID.randomUUID());
        educationCategory.setName("Education");
        educationCategory.setDescription("Books, courses, supplies");
        educationCategory.setColor("#96CEB4");
        educationCategory.setIcon("book");
        educationCategory.setIsActive(true);

        categories.addAll(Arrays.asList(foodCategory, transportCategory, entertainmentCategory, educationCategory));

        // Sample expenses for budget calculations
        Expense expense1 = new Expense();
        expense1.setId(UUID.randomUUID());
        expense1.setTitle("Lunch at Campus Cafeteria");
        expense1.setAmount(new BigDecimal("12.50"));
        expense1.setExpenseDate(java.time.LocalDate.now());
        expense1.setCategory(foodCategory);

        Expense expense2 = new Expense();
        expense2.setId(UUID.randomUUID());
        expense2.setTitle("Bus Pass");
        expense2.setAmount(new BigDecimal("85.00"));
        expense2.setExpenseDate(java.time.LocalDate.now().minusDays(2));
        expense2.setCategory(transportCategory);

        Expense expense3 = new Expense();
        expense3.setId(UUID.randomUUID());
        expense3.setTitle("Netflix Subscription");
        expense3.setAmount(new BigDecimal("15.99"));
        expense3.setExpenseDate(java.time.LocalDate.now().minusDays(5));
        expense3.setCategory(entertainmentCategory);

        // Add an Education expense so it appears in the spending distribution
        Expense expense4 = new Expense();
        expense4.setId(UUID.randomUUID());
        expense4.setTitle("Textbook - Programming");
        expense4.setAmount(new BigDecimal("89.99"));
        expense4.setExpenseDate(java.time.LocalDate.now().minusDays(3));
        expense4.setCategory(educationCategory);
        expense4.setPaymentMethod("card");

        expenses.addAll(Arrays.asList(expense1, expense2, expense3, expense4));

        // Sample budget limits
        BudgetLimit foodBudget = new BudgetLimit();
        foodBudget.setId(UUID.randomUUID());
        foodBudget.setCategory(foodCategory);
        foodBudget.setBudgetMonth(java.time.LocalDate.now().getMonthValue());
        foodBudget.setBudgetYear(java.time.LocalDate.now().getYear());
        foodBudget.setLimitAmount(new BigDecimal("200.00"));
        foodBudget.setAlertThreshold(new BigDecimal("75.0"));

        BudgetLimit transportBudget = new BudgetLimit();
        transportBudget.setId(UUID.randomUUID());
        transportBudget.setCategory(transportCategory);
        transportBudget.setBudgetMonth(java.time.LocalDate.now().getMonthValue());
        transportBudget.setBudgetYear(java.time.LocalDate.now().getYear());
        transportBudget.setLimitAmount(new BigDecimal("150.00"));
        transportBudget.setAlertThreshold(new BigDecimal("80.0"));

        // Optionally, add a budget limit for Education in the current month
        BudgetLimit educationBudget = new BudgetLimit();
        educationBudget.setId(UUID.randomUUID());
        educationBudget.setCategory(educationCategory);
        educationBudget.setBudgetMonth(java.time.LocalDate.now().getMonthValue());
        educationBudget.setBudgetYear(java.time.LocalDate.now().getYear());
        educationBudget.setLimitAmount(new BigDecimal("120.00"));
        educationBudget.setAlertThreshold(new BigDecimal("80.0"));

        budgetLimits.addAll(Arrays.asList(foodBudget, transportBudget, educationBudget));

        // Sample financial goals
        FinancialGoal emergencyFund = new FinancialGoal();
        emergencyFund.setId(UUID.randomUUID());
        emergencyFund.setGoalName("Emergency Fund");
        emergencyFund.setDescription("Save for unexpected expenses");
        emergencyFund.setTargetAmount(new BigDecimal("1000.00"));
        emergencyFund.setCurrentAmount(new BigDecimal("250.00"));
        emergencyFund.setTargetDate(java.time.LocalDate.now().plusMonths(6));
        emergencyFund.setGoalType("savings");
        emergencyFund.setIsActive(true);

        FinancialGoal laptop = new FinancialGoal();
        laptop.setId(UUID.randomUUID());
        laptop.setGoalName("New Laptop");
        laptop.setDescription("Save for a new laptop for studies");
        laptop.setTargetAmount(new BigDecimal("800.00"));
        laptop.setCurrentAmount(new BigDecimal("150.00"));
        laptop.setTargetDate(java.time.LocalDate.now().plusMonths(4));
        laptop.setGoalType("purchase");
        laptop.setIsActive(true);

        financialGoals.addAll(Arrays.asList(emergencyFund, laptop));
    }

    private void refreshData() {
        budgetView.updateBudgetLimits(budgetLimits);
        budgetView.updateFinancialGoals(financialGoals);
        updateAnalytics();
    }

    private void updateAnalytics() {
        int month = budgetView.getSelectedMonth();
        int year = budgetView.getSelectedYear();
        YearMonth selectedYm = YearMonth.of(year, month);

        // Calculate monthly totals (selected month)
        BigDecimal totalSpent = expenses.stream()
            .filter(e -> e.getExpenseDate().getMonthValue() == month &&
                         e.getExpenseDate().getYear() == year)
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBudget = budgetLimits.stream()
            .filter(b -> b.getBudgetMonth() == month && b.getBudgetYear() == year)
            .map(BudgetLimit::getLimitAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        budgetView.updateSummary(totalBudget, totalSpent, totalBudget.subtract(totalSpent));

        // Build spending distribution first (preferred)
        Map<String, BigDecimal> spendingByCategory = new LinkedHashMap<>();
        expenses.stream()
            .filter(e -> e.getExpenseDate().getMonthValue() == month && e.getExpenseDate().getYear() == year)
            .forEach(e -> {
                String name = (e.getCategory() != null && e.getCategory().getName() != null)
                    ? e.getCategory().getName() : "Uncategorized";
                spendingByCategory.merge(name, e.getAmount(), BigDecimal::add);
            });

        // Month budgets (for fallback/alerts)
        List<BudgetLimit> monthBudgets = budgetLimits.stream()
            .filter(b -> b.getBudgetMonth() == month && b.getBudgetYear() == year)
            .collect(Collectors.toList());

        Map<String, BigDecimal> categoryData;
        if (!spendingByCategory.isEmpty()) {
            // Prefer spending distribution so all spent categories appear
            categoryData = spendingByCategory;
        } else if (!monthBudgets.isEmpty()) {
            // Fallback to budget allocation when there is no spending this month
            categoryData = new LinkedHashMap<>();
            for (BudgetLimit b : monthBudgets) {
                String name = b.getCategory() != null ? b.getCategory().getName() : "Uncategorized";
                categoryData.merge(name, b.getLimitAmount(), BigDecimal::add);
            }
        } else {
            categoryData = new LinkedHashMap<>();
            categoryData.put("No data", BigDecimal.ONE);
        }
        // Build color map from categories list (name -> hex color)
        Map<String, String> colorByCategory = new LinkedHashMap<>();
        for (ExpenseCategory c : categories) {
            if (c != null && c.getName() != null && c.getColor() != null && !c.getColor().isBlank()) {
                colorByCategory.put(c.getName(), c.getColor());
            }
        }
        budgetView.updateCategoryChart(categoryData, colorByCategory);

        // Spending trend: last 6 months relative to selected month
        List<Map<String, Object>> trendData = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = selectedYm.minusMonths(i);
            BigDecimal monthSpend = expenses.stream()
                .filter(e -> e.getExpenseDate().getYear() == ym.getYear() && e.getExpenseDate().getMonthValue() == ym.getMonthValue())
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal monthBudget = budgetLimits.stream()
                .filter(b -> b.getBudgetYear() == ym.getYear() && b.getBudgetMonth() == ym.getMonthValue())
                .map(BudgetLimit::getLimitAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> row = new HashMap<>();
            row.put("month", ym.getMonth().name().substring(0, 3) + " " + ym.getYear());
            row.put("total", monthSpend);
            row.put("budget", monthBudget);
            trendData.add(row);
        }
        budgetView.updateSpendingTrendChart(trendData);

        // Budget alerts for selected month
        List<String> alerts = new ArrayList<>();
        monthBudgets.forEach(budget -> {
            BigDecimal spent = expenses.stream()
                .filter(e -> e.getCategory() != null && budget.getCategory() != null &&
                             e.getCategory().equals(budget.getCategory()) &&
                             e.getExpenseDate().getMonthValue() == month &&
                             e.getExpenseDate().getYear() == year)
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (budget.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentage = spent.multiply(BigDecimal.valueOf(100))
                    .divide(budget.getLimitAmount(), 2, BigDecimal.ROUND_HALF_UP);
                if (percentage.compareTo(budget.getAlertThreshold()) >= 0) {
                    alerts.add(String.format("%s: %.1f%% of budget used ($%.2f / $%.2f)",
                        budget.getCategory() != null ? budget.getCategory().getName() : "Uncategorized",
                        percentage.doubleValue(), spent.doubleValue(), budget.getLimitAmount().doubleValue()));
                }
            }
        });
        // Goal deadline alerts (unchanged)
        financialGoals.stream().filter(FinancialGoal::getIsActive).forEach(goal -> {
            if (goal.getTargetDate() != null && goal.getTargetDate().isBefore(java.time.LocalDate.now().plusMonths(1))) {
                BigDecimal progress = goal.getProgressPercentage();
                if (progress.compareTo(BigDecimal.valueOf(90)) < 0) {
                    alerts.add(String.format("Goal '%s' deadline approaching - %.1f%% complete",
                        goal.getGoalName(), progress.doubleValue()));
                }
            }
        });
        budgetView.updateAlerts(alerts);
    }

    // Budget dialog handlers
    private void showAddBudgetDialog(BudgetLimit budgetLimit) {
        BudgetDialog dialog = new BudgetDialog(budgetLimit, categories);
        Optional<BudgetLimit> result = dialog.showAndWait();

        result.ifPresent(newBudget -> {
            newBudget.setId(UUID.randomUUID());
            budgetLimits.add(newBudget);
            refreshData();
            showSuccessAlert("Budget limit set successfully!");
        });
    }

    private void showEditBudgetDialog(BudgetLimit budgetLimit) {
        BudgetDialog dialog = new BudgetDialog(budgetLimit, categories);
        Optional<BudgetLimit> result = dialog.showAndWait();

        result.ifPresent(updatedBudget -> {
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

    public BudgetDashboardView getView() {
        return budgetView;
    }
}
