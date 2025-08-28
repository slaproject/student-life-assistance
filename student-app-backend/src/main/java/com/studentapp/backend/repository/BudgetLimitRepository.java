package com.studentapp.backend.repository;

import com.studentapp.common.model.BudgetLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetLimitRepository extends JpaRepository<BudgetLimit, UUID> {

    List<BudgetLimit> findByUserId(UUID userId);

    List<BudgetLimit> findByUserIdAndBudgetYearAndBudgetMonth(UUID userId, Integer budgetYear, Integer budgetMonth);

    Optional<BudgetLimit> findByUserIdAndCategoryIdAndBudgetYearAndBudgetMonth(
        UUID userId, UUID categoryId, Integer budgetYear, Integer budgetMonth);

    @Query("SELECT bl FROM BudgetLimit bl WHERE bl.userId = :userId AND bl.budgetYear = :year")
    List<BudgetLimit> findByUserIdAndYear(@Param("userId") UUID userId, @Param("year") Integer year);

    @Query("SELECT COUNT(bl) FROM BudgetLimit bl WHERE bl.userId = :userId")
    Long countByUserId(@Param("userId") UUID userId);
}
