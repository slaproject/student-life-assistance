package com.studentapp.backend.service;

import com.studentapp.common.model.Task;
import com.studentapp.common.model.TaskColumn;
import com.studentapp.common.model.TaskAttachment;
import com.studentapp.common.model.Task.Priority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface TaskService {

    // Task Column operations
    TaskColumn saveColumn(TaskColumn column, UUID userId);
    Optional<TaskColumn> getColumnById(UUID id, UUID userId);
    List<TaskColumn> getAllColumns(UUID userId);
    void deleteColumn(UUID id, UUID userId);
    TaskColumn updateColumnPosition(UUID id, Integer newPosition, UUID userId);
    void initializeDefaultColumns(UUID userId);

    // Task operations
    Task saveTask(Task task, UUID userId);
    Optional<Task> getTaskById(UUID id, UUID userId);
    List<Task> getAllTasks(UUID userId);
    List<Task> getTasksByColumn(UUID userId, UUID columnId);
    List<Task> getTasksByPriority(UUID userId, Priority priority);
    List<Task> getUpcomingTasks(UUID userId, int days);
    List<Task> getOverdueTasks(UUID userId);
    List<Task> searchTasks(UUID userId, String searchTerm);
    void deleteTask(UUID id, UUID userId);
    Task updateTaskPosition(UUID taskId, UUID newColumnId, Integer newPosition, UUID userId);
    Task moveTaskToColumn(UUID taskId, UUID columnId, UUID userId);

    // Task Attachment operations
    TaskAttachment saveAttachment(TaskAttachment attachment, UUID userId);
    Optional<TaskAttachment> getAttachmentById(UUID id, UUID userId);
    List<TaskAttachment> getAttachmentsByTask(UUID taskId, UUID userId);
    void deleteAttachment(UUID id, UUID userId);

    // Analytics and reporting
    Map<String, Long> getTaskStatistics(UUID userId);
    Map<Priority, Long> getTaskCountByPriority(UUID userId);
    Map<String, Long> getTaskCountByStatus(UUID userId);
    List<Task> getTasksDueSoon(UUID userId, int days);
    
    // Bulk operations
    void moveMultipleTasks(List<UUID> taskIds, UUID targetColumnId, UUID userId);
    void deleteMultipleTasks(List<UUID> taskIds, UUID userId);
    void updateMultipleTasksPriority(List<UUID> taskIds, Priority priority, UUID userId);
}