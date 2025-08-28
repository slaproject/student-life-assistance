package com.studentapp.backend.service;

import com.studentapp.backend.repository.*;
import com.studentapp.common.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FinanceServiceImpl implements FinanceService {

    @Autowired
    private ExpenseCategoryRepository categoryRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetLimitRepository budgetLimitRepository;

    @Autowired
    private FinancialGoalRepository financialGoalRepository;

    // Expense Category operations
    @Override
    public ExpenseCategory saveCategory(ExpenseCategory category, UUID userId) {
        category.setUserId(userId);
        return categoryRepository.save(category);
    }

    @Override
    public Optional<ExpenseCategory> getCategoryById(UUID id, UUID userId) {
        return categoryRepository.findById(id)
            .filter(category -> category.getUserId().equals(userId));
    }

    @Override
    public List<ExpenseCategory> getAllCategories(UUID userId) {
        return categoryRepository.findByUserId(userId);
    }

    @Override
    public List<ExpenseCategory> getActiveCategories(UUID userId) {
        return categoryRepository.findByUserIdAndIsActiveTrue(userId);
    }

    @Override
    public void deleteCategory(UUID id, UUID userId) {
        categoryRepository.findById(id)
            .filter(category -> category.getUserId().equals(userId))
            .ifPresent(category -> {
                category.setIsActive(false);
                categoryRepository.save(category);
            });
    }

    // Expense operations
    @Override
    public Expense saveExpense(Expense expense, UUID userId) {
        expense.setUserId(userId);
        return expenseRepository.save(expense);
    }

    @Override
    public Optional<Expense> getExpenseById(UUID id, UUID userId) {
        return expenseRepository.findById(id)
            .filter(expense -> expense.getUserId().equals(userId));
    }

    @Override
    public List<Expense> getAllExpenses(UUID userId) {
        return expenseRepository.findByUserIdOrderByExpenseDateDesc(userId);
    }

    @Override
    public List<Expense> getExpensesByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(userId, startDate, endDate);
    }

    @Override
    public List<Expense> getExpensesByCategory(UUID categoryId, UUID userId) {
        return expenseRepository.findByCategoryIdOrderByExpenseDateDesc(categoryId)
            .stream()
            .filter(expense -> expense.getUserId().equals(userId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Expense> getExpensesByMonth(UUID userId, int month, int year) {
        return expenseRepository.findByUserIdAndMonth(userId, month, year);
    }

    @Override
    public void deleteExpense(UUID id, UUID userId) {
        expenseRepository.findById(id)
            .filter(expense -> expense.getUserId().equals(userId))
            .ifPresent(expenseRepository::delete);
    }

    // Budget Limit operations
    @Override
    public BudgetLimit saveBudgetLimit(BudgetLimit budgetLimit, UUID userId) {
        budgetLimit.setUserId(userId);
        return budgetLimitRepository.save(budgetLimit);
    }

    @Override
    public Optional<BudgetLimit> getBudgetLimitById(UUID id, UUID userId) {
        return budgetLimitRepository.findById(id)
            .filter(budgetLimit -> budgetLimit.getUserId().equals(userId));
    }

    @Override
    public List<BudgetLimit> getBudgetLimitsByMonth(UUID userId, int month, int year) {
        return budgetLimitRepository.findByUserIdAndBudgetYearAndBudgetMonth(userId, year, month);
    }

    @Override
    public Optional<BudgetLimit> getBudgetLimitForCategory(UUID userId, UUID categoryId, int month, int year) {
        return budgetLimitRepository.findByUserIdAndCategoryIdAndBudgetYearAndBudgetMonth(userId, categoryId, year, month);
    }

    @Override
    public void deleteBudgetLimit(UUID id, UUID userId) {
        budgetLimitRepository.findById(id)
            .filter(budgetLimit -> budgetLimit.getUserId().equals(userId))
            .ifPresent(budgetLimitRepository::delete);
    }

    // Financial Goal operations
    @Override
    public FinancialGoal saveFinancialGoal(FinancialGoal goal, UUID userId) {
        goal.setUserId(userId);
        return financialGoalRepository.save(goal);
    }

    @Override
    public Optional<FinancialGoal> getFinancialGoalById(UUID id, UUID userId) {
        return financialGoalRepository.findById(id)
            .filter(goal -> goal.getUserId().equals(userId));
    }

    @Override
    public List<FinancialGoal> getAllFinancialGoals(UUID userId) {
        return financialGoalRepository.findByUserId(userId);
    }

    @Override
    public List<FinancialGoal> getActiveFinancialGoals(UUID userId) {
        return financialGoalRepository.findByUserIdAndIsActiveTrue(userId);
    }

    @Override
    public void deleteFinancialGoal(UUID id, UUID userId) {
        financialGoalRepository.findById(id)
            .filter(goal -> goal.getUserId().equals(userId))
            .ifPresent(goal -> {
                goal.setIsActive(false);
                financialGoalRepository.save(goal);
            });
    }

    // Analytics and reporting
    @Override
    public BigDecimal getTotalExpensesByMonth(UUID userId, int month, int year) {
        BigDecimal total = expenseRepository.getTotalExpensesByUserAndMonth(userId, month, year);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public Map<String, BigDecimal> getCategoryWiseExpenses(UUID userId, int month, int year) {
        List<Object[]> results = expenseRepository.getCategoryWiseExpensesByMonth(userId, month, year);
        Map<String, BigDecimal> categoryExpenses = new HashMap<>();

        for (Object[] result : results) {
            String categoryName = (String) result[1];
            BigDecimal amount = (BigDecimal) result[2];
            categoryExpenses.put(categoryName, amount);
        }

        return categoryExpenses;
    }

    @Override
    public Map<String, Object> getBudgetAnalysis(UUID userId, int month, int year) {
        Map<String, Object> analysis = new HashMap<>();

        List<BudgetLimit> budgetLimits = getBudgetLimitsByMonth(userId, month, year);
        BigDecimal totalBudget = budgetLimits.stream()
            .map(BudgetLimit::getLimitAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSpent = getTotalExpensesByMonth(userId, month, year);
        BigDecimal remaining = totalBudget.subtract(totalSpent);

        analysis.put("totalBudget", totalBudget);
        analysis.put("totalSpent", totalSpent);
        analysis.put("remaining", remaining);
        analysis.put("percentageUsed", totalBudget.compareTo(BigDecimal.ZERO) > 0 ?
            totalSpent.multiply(BigDecimal.valueOf(100)).divide(totalBudget, 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);

        return analysis;
    }

    @Override
    public Map<String, Object> getSpendingTrends(UUID userId, int months) {
        Map<String, Object> trends = new HashMap<>();
        List<Map<String, Object>> monthlyData = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();
        for (int i = months - 1; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(currentDate.minusMonths(i));
            BigDecimal monthlyTotal = getTotalExpensesByMonth(userId, yearMonth.getMonthValue(), yearMonth.getYear());

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", yearMonth.toString());
            monthData.put("total", monthlyTotal);
            monthlyData.add(monthData);
        }

        trends.put("monthlyTotals", monthlyData);
        return trends;
    }

    @Override
    public List<Map<String, Object>> getBudgetAlerts(UUID userId) {
        List<Map<String, Object>> alerts = new ArrayList<>();
        LocalDate now = LocalDate.now();

        List<BudgetLimit> currentBudgets = getBudgetLimitsByMonth(userId, now.getMonthValue(), now.getYear());

        for (BudgetLimit budget : currentBudgets) {
            BigDecimal spent = expenseRepository.getTotalExpensesByCategoryAndMonth(
                budget.getCategory().getId(), now.getMonthValue(), now.getYear());

            if (spent != null && budget.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentageUsed = spent.multiply(BigDecimal.valueOf(100))
                    .divide(budget.getLimitAmount(), 2, BigDecimal.ROUND_HALF_UP);

                if (percentageUsed.compareTo(budget.getAlertThreshold()) >= 0) {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("categoryName", budget.getCategory().getName());
                    alert.put("budgetLimit", budget.getLimitAmount());
                    alert.put("spent", spent);
                    alert.put("percentageUsed", percentageUsed);
                    alert.put("alertThreshold", budget.getAlertThreshold());
                    alerts.add(alert);
                }
            }
        }

        return alerts;
    }
}
