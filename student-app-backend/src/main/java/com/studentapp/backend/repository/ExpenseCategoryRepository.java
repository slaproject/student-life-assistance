package com.studentapp.backend.repository;

import com.studentapp.common.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, UUID> {

    List<ExpenseCategory> findByUserIdAndIsActiveTrue(UUID userId);

    List<ExpenseCategory> findByUserId(UUID userId);

    @Query("SELECT ec FROM ExpenseCategory ec WHERE ec.userId = :userId AND ec.name = :name")
    ExpenseCategory findByUserIdAndName(@Param("userId") UUID userId, @Param("name") String name);

    @Query("SELECT COUNT(ec) FROM ExpenseCategory ec WHERE ec.userId = :userId AND ec.isActive = true")
    Long countActiveByUserId(@Param("userId") UUID userId);
}
