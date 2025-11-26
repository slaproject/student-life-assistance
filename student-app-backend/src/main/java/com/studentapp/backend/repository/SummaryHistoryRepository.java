package com.studentapp.backend.repository;

import com.studentapp.backend.model.SummaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SummaryHistoryRepository extends JpaRepository<SummaryHistory, UUID> {
    List<SummaryHistory> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
