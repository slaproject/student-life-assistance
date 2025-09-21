package com.studentapp.backend.dto;

import com.studentapp.common.model.Task;
import com.studentapp.common.model.Task.Priority;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Task to avoid serialization issues with circular references
 */
public class TaskDto {
    private UUID id;
    private UUID userId;
    private UUID columnId;
    private String title;
    private String description;
    private Priority priority;
    private LocalDateTime dueDate;
    private Integer position;
    private String tags;
    private UUID assignedTo;
    private UUID projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskDto() {}

    public TaskDto(Task task) {
        this.id = task.getId();
        this.userId = task.getUserId();
        this.columnId = task.getColumn() != null ? task.getColumn().getId() : null;
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.priority = task.getPriority();
        this.dueDate = task.getDueDate();
        this.position = task.getPosition();
        this.tags = task.getTags();
        this.assignedTo = task.getAssignedTo();
        this.projectId = task.getProjectId();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getColumnId() { return columnId; }
    public void setColumnId(UUID columnId) { this.columnId = columnId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public UUID getAssignedTo() { return assignedTo; }
    public void setAssignedTo(UUID assignedTo) { this.assignedTo = assignedTo; }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}