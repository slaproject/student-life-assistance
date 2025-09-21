package com.studentapp.backend.repository;

import com.studentapp.common.model.Task;
import com.studentapp.common.model.Task.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByUserIdOrderByPositionAsc(UUID userId);

    List<Task> findByUserIdAndColumn_IdOrderByPositionAsc(UUID userId, UUID columnId);

    List<Task> findByUserIdAndPriorityOrderByCreatedAtDesc(UUID userId, Priority priority);

    List<Task> findByUserIdAndDueDateBetweenOrderByDueDateAsc(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.dueDate < :currentDate ORDER BY t.dueDate ASC")
    List<Task> findOverdueTasksByUserId(@Param("userId") UUID userId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.dueDate BETWEEN :startDate AND :endDate ORDER BY t.dueDate ASC")
    List<Task> findUpcomingTasksByUserId(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) ORDER BY t.createdAt DESC")
    List<Task> searchTasksByUserId(@Param("userId") UUID userId, @Param("searchTerm") String searchTerm);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId")
    Long countByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.column.title = 'Done'")
    Long countCompletedTasksByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.dueDate < :currentDate")
    Long countOverdueTasksByUserId(@Param("userId") UUID userId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT t.priority, COUNT(t) FROM Task t WHERE t.userId = :userId GROUP BY t.priority")
    List<Object[]> getTaskCountByPriority(@Param("userId") UUID userId);

    @Query("SELECT t.column.title, COUNT(t) FROM Task t WHERE t.userId = :userId GROUP BY t.column.title")
    List<Object[]> getTaskCountByStatus(@Param("userId") UUID userId);

    @Query("SELECT MAX(t.position) FROM Task t WHERE t.userId = :userId AND t.column.id = :columnId")
    Integer findMaxPositionByUserIdAndColumnId(@Param("userId") UUID userId, @Param("columnId") UUID columnId);

    List<Task> findByAssignedToOrderByCreatedAtDesc(UUID assignedTo);
}