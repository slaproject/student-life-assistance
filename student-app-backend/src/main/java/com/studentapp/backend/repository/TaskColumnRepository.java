package com.studentapp.backend.repository;

import com.studentapp.common.model.TaskColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskColumnRepository extends JpaRepository<TaskColumn, UUID> {

    List<TaskColumn> findByUserIdOrderByPositionAsc(UUID userId);

    @Query("SELECT tc FROM TaskColumn tc WHERE tc.userId = :userId AND tc.position = :position")
    TaskColumn findByUserIdAndPosition(@Param("userId") UUID userId, @Param("position") Integer position);

    @Query("SELECT MAX(tc.position) FROM TaskColumn tc WHERE tc.userId = :userId")
    Integer findMaxPositionByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(tc) FROM TaskColumn tc WHERE tc.userId = :userId")
    Long countByUserId(@Param("userId") UUID userId);

    boolean existsByUserIdAndTitle(UUID userId, String title);

    @Modifying
    @Query("UPDATE TaskColumn tc SET tc.position = tc.position + 1 WHERE tc.userId = :userId AND tc.position >= :fromPosition")
    void incrementPositionsFrom(@Param("userId") UUID userId, @Param("fromPosition") Integer fromPosition);

    @Query("SELECT tc FROM TaskColumn tc WHERE tc.userId = :userId AND tc.position >= :fromPosition ORDER BY tc.position ASC")
    List<TaskColumn> findByUserIdAndPositionGreaterThanEqual(@Param("userId") UUID userId, @Param("fromPosition") Integer fromPosition);
}