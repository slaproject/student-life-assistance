package com.studentapp.backend.repository;

import com.studentapp.common.model.FinancialGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, UUID> {

    List<FinancialGoal> findByUserIdAndIsActiveTrue(UUID userId);

    List<FinancialGoal> findByUserId(UUID userId);

    List<FinancialGoal> findByUserIdAndGoalType(UUID userId, String goalType);

    @Query("SELECT fg FROM FinancialGoal fg WHERE fg.userId = :userId AND fg.targetDate <= :date AND fg.isActive = true")
    List<FinancialGoal> findByUserIdAndTargetDateBefore(@Param("userId") UUID userId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(fg) FROM FinancialGoal fg WHERE fg.userId = :userId AND fg.isActive = true")
    Long countActiveByUserId(@Param("userId") UUID userId);
}
