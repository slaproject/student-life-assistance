package com.studentapp.backend.controller;

import com.studentapp.backend.service.FinanceService;
import com.studentapp.common.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "*")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    // Expense Category endpoints
    @GetMapping("/categories")
    public ResponseEntity<List<ExpenseCategory>> getAllCategories(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<ExpenseCategory> categories = financeService.getActiveCategories(userId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ExpenseCategory> getCategoryById(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Optional<ExpenseCategory> category = financeService.getCategoryById(id, userId);
        return category.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/categories")
    public ResponseEntity<ExpenseCategory> createCategory(@RequestBody ExpenseCategory category, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        ExpenseCategory savedCategory = financeService.saveCategory(category, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ExpenseCategory> updateCategory(@PathVariable UUID id, @RequestBody ExpenseCategory category, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Optional<ExpenseCategory> existingCategory = financeService.getCategoryById(id, userId);

        if (existingCategory.isPresent()) {
            category.setId(id);
            ExpenseCategory updatedCategory = financeService.saveCategory(category, userId);
            return ResponseEntity.ok(updatedCategory);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        financeService.deleteCategory(id, userId);
        return ResponseEntity.noContent().build();
    }

    // Expense endpoints
    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> getAllExpenses(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<Expense> expenses = financeService.getAllExpenses(userId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/expenses/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Optional<Expense> expense = financeService.getExpenseById(id, userId);
        return expense.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/expenses/month/{year}/{month}")
    public ResponseEntity<List<Expense>> getExpensesByMonth(@PathVariable int year, @PathVariable int month, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<Expense> expenses = financeService.getExpensesByMonth(userId, month, year);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/expenses/category/{categoryId}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(@PathVariable UUID categoryId, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<Expense> expenses = financeService.getExpensesByCategory(categoryId, userId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/expenses/daterange")
    public ResponseEntity<List<Expense>> getExpensesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<Expense> expenses = financeService.getExpensesByDateRange(userId, start, end);
        return ResponseEntity.ok(expenses);
    }

    @PostMapping("/expenses")
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Expense savedExpense = financeService.saveExpense(expense, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable UUID id, @RequestBody Expense expense, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Optional<Expense> existingExpense = financeService.getExpenseById(id, userId);

        if (existingExpense.isPresent()) {
            expense.setId(id);
            Expense updatedExpense = financeService.saveExpense(expense, userId);
            return ResponseEntity.ok(updatedExpense);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        financeService.deleteExpense(id, userId);
        return ResponseEntity.noContent().build();
    }

    // Budget Limit endpoints
    @GetMapping("/budget-limits")
    public ResponseEntity<List<BudgetLimit>> getBudgetLimitsByMonth(
            @RequestParam int month,
            @RequestParam int year,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<BudgetLimit> budgetLimits = financeService.getBudgetLimitsByMonth(userId, month, year);
        return ResponseEntity.ok(budgetLimits);
    }

    @GetMapping("/budget-limits/{id}")
    public ResponseEntity<BudgetLimit> getBudgetLimitById(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Optional<BudgetLimit> budgetLimit = financeService.getBudgetLimitById(id, userId);
        return budgetLimit.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/budget-limits")
    public ResponseEntity<BudgetLimit> createBudgetLimit(@RequestBody BudgetLimit budgetLimit, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        BudgetLimit savedBudgetLimit = financeService.saveBudgetLimit(budgetLimit, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBudgetLimit);
    }

    @PutMapping("/budget-limits/{id}")
    public ResponseEntity<BudgetLimit> updateBudgetLimit(@PathVariable UUID id, @RequestBody BudgetLimit budgetLimit, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Optional<BudgetLimit> existingBudgetLimit = financeService.getBudgetLimitById(id, userId);

        if (existingBudgetLimit.isPresent()) {
            budgetLimit.setId(id);
            BudgetLimit updatedBudgetLimit = financeService.saveBudgetLimit(budgetLimit, userId);
            return ResponseEntity.ok(updatedBudgetLimit);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/budget-limits/{id}")
    public ResponseEntity<Void> deleteBudgetLimit(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        financeService.deleteBudgetLimit(id, userId);
        return ResponseEntity.noContent().build();
    }

    // Financial Goal endpoints
    @GetMapping("/goals")
    public ResponseEntity<List<FinancialGoal>> getAllFinancialGoals(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<FinancialGoal> goals = financeService.getActiveFinancialGoals(userId);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/goals/{id}")
    public ResponseEntity<FinancialGoal> getFinancialGoalById(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Optional<FinancialGoal> goal = financeService.getFinancialGoalById(id, userId);
        return goal.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/goals")
    public ResponseEntity<FinancialGoal> createFinancialGoal(@RequestBody FinancialGoal goal, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        FinancialGoal savedGoal = financeService.saveFinancialGoal(goal, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGoal);
    }

    @PutMapping("/goals/{id}")
    public ResponseEntity<FinancialGoal> updateFinancialGoal(@PathVariable UUID id, @RequestBody FinancialGoal goal, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Optional<FinancialGoal> existingGoal = financeService.getFinancialGoalById(id, userId);

        if (existingGoal.isPresent()) {
            goal.setId(id);
            FinancialGoal updatedGoal = financeService.saveFinancialGoal(goal, userId);
            return ResponseEntity.ok(updatedGoal);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/goals/{id}")
    public ResponseEntity<Void> deleteFinancialGoal(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        financeService.deleteFinancialGoal(id, userId);
        return ResponseEntity.noContent().build();
    }

    // Analytics and reporting endpoints
    @GetMapping("/analytics/monthly-total")
    public ResponseEntity<BigDecimal> getMonthlyTotal(
            @RequestParam int month,
            @RequestParam int year,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        BigDecimal total = financeService.getTotalExpensesByMonth(userId, month, year);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/analytics/category-wise")
    public ResponseEntity<Map<String, BigDecimal>> getCategoryWiseExpenses(
            @RequestParam int month,
            @RequestParam int year,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Map<String, BigDecimal> categoryExpenses = financeService.getCategoryWiseExpenses(userId, month, year);
        return ResponseEntity.ok(categoryExpenses);
    }

    @GetMapping("/analytics/budget-analysis")
    public ResponseEntity<Map<String, Object>> getBudgetAnalysis(
            @RequestParam int month,
            @RequestParam int year,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Map<String, Object> analysis = financeService.getBudgetAnalysis(userId, month, year);
        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/analytics/spending-trends")
    public ResponseEntity<Map<String, Object>> getSpendingTrends(
            @RequestParam(defaultValue = "6") int months,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Map<String, Object> trends = financeService.getSpendingTrends(userId, months);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/analytics/budget-alerts")
    public ResponseEntity<List<Map<String, Object>>> getBudgetAlerts(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<Map<String, Object>> alerts = financeService.getBudgetAlerts(userId);
        return ResponseEntity.ok(alerts);
    }
}
