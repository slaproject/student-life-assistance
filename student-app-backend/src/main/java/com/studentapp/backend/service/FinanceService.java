package com.studentapp.backend.service;

import com.studentapp.common.model.Expense;
import com.studentapp.common.model.ExpenseCategory;
import com.studentapp.common.model.BudgetLimit;
import com.studentapp.common.model.FinancialGoal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface FinanceService {

    // Expense Category operations
    ExpenseCategory saveCategory(ExpenseCategory category, UUID userId);
    ExpenseCategory saveCategory(ExpenseCategory category); // For internal use when userId already set
    Optional<ExpenseCategory> getCategoryById(UUID id, UUID userId);
    List<ExpenseCategory> getAllCategories(UUID userId);
    List<ExpenseCategory> getActiveCategories(UUID userId);
    void deleteCategory(UUID id, UUID userId);

    // Expense operations
    Expense saveExpense(Expense expense, UUID userId);
    Optional<Expense> getExpenseById(UUID id, UUID userId);
    List<Expense> getAllExpenses(UUID userId);
    List<Expense> getExpensesByDateRange(UUID userId, LocalDate startDate, LocalDate endDate);
    List<Expense> getExpensesByCategory(UUID categoryId, UUID userId);
    List<Expense> getExpensesByMonth(UUID userId, int month, int year);
    void deleteExpense(UUID id, UUID userId);

    // Budget Limit operations
    BudgetLimit saveBudgetLimit(BudgetLimit budgetLimit, UUID userId);
    Optional<BudgetLimit> getBudgetLimitById(UUID id, UUID userId);
    List<BudgetLimit> getBudgetLimitsByMonth(UUID userId, int month, int year);
    Optional<BudgetLimit> getBudgetLimitForCategory(UUID userId, UUID categoryId, int month, int year);
    void deleteBudgetLimit(UUID id, UUID userId);

    // Financial Goal operations
    FinancialGoal saveFinancialGoal(FinancialGoal goal, UUID userId);
    Optional<FinancialGoal> getFinancialGoalById(UUID id, UUID userId);
    List<FinancialGoal> getAllFinancialGoals(UUID userId);
    List<FinancialGoal> getActiveFinancialGoals(UUID userId);
    void deleteFinancialGoal(UUID id, UUID userId);

    // Analytics and reporting
    BigDecimal getTotalExpensesByMonth(UUID userId, int month, int year);
    Map<String, BigDecimal> getCategoryWiseExpenses(UUID userId, int month, int year);
    Map<String, Object> getBudgetAnalysis(UUID userId, int month, int year);
    Map<String, Object> getSpendingTrends(UUID userId, int months);
    List<Map<String, Object>> getBudgetAlerts(UUID userId);
}
