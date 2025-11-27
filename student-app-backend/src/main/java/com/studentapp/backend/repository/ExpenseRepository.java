package com.studentapp.backend.repository;

import com.studentapp.common.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.userId = :userId ORDER BY e.expenseDate DESC")
    List<Expense> findByUserIdOrderByExpenseDateDesc(@Param("userId") UUID userId);

    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.userId = :userId AND e.expenseDate BETWEEN :startDate AND :endDate ORDER BY e.expenseDate DESC")
    List<Expense> findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(
        @Param("userId") UUID userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.category.id = :categoryId ORDER BY e.expenseDate DESC")
    List<Expense> findByCategoryIdOrderByExpenseDateDesc(@Param("categoryId") UUID categoryId);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND EXTRACT(MONTH FROM e.expenseDate) = :month AND EXTRACT(YEAR FROM e.expenseDate) = :year ORDER BY e.expenseDate DESC")
    List<Expense> findByUserIdAndMonth(@Param("userId") UUID userId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.userId = :userId AND EXTRACT(MONTH FROM e.expenseDate) = :month AND EXTRACT(YEAR FROM e.expenseDate) = :year")
    BigDecimal getTotalExpensesByUserAndMonth(@Param("userId") UUID userId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.category.id = :categoryId AND EXTRACT(MONTH FROM e.expenseDate) = :month AND EXTRACT(YEAR FROM e.expenseDate) = :year")
    BigDecimal getTotalExpensesByCategoryAndMonth(@Param("categoryId") UUID categoryId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT e.category.id, e.category.name, SUM(e.amount) FROM Expense e WHERE e.userId = :userId AND EXTRACT(MONTH FROM e.expenseDate) = :month AND EXTRACT(YEAR FROM e.expenseDate) = :year GROUP BY e.category.id, e.category.name")
    List<Object[]> getCategoryWiseExpensesByMonth(@Param("userId") UUID userId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.userId = :userId")
    Long countByUserId(@Param("userId") UUID userId);
}
