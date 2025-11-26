package com.studentapp.backend.repository;

import com.studentapp.backend.model.UsageEntity;
import com.studentapp.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsageRepository extends JpaRepository<UsageEntity, UUID> {
    Optional<UsageEntity> findByUserAndYearMonth(User user, String yearMonth);
}
