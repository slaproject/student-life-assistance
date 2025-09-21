package com.studentapp.backend.dto;

import com.studentapp.common.model.TaskColumn;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for TaskColumn to avoid serialization issues with circular references
 */
public class TaskColumnDto {
    private UUID id;
    private UUID userId;
    private String title;
    private String color;
    private Integer position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskColumnDto() {}

    public TaskColumnDto(TaskColumn taskColumn) {
        this.id = taskColumn.getId();
        this.userId = taskColumn.getUserId();
        this.title = taskColumn.getTitle();
        this.color = taskColumn.getColor();
        this.position = taskColumn.getPosition();
        this.createdAt = taskColumn.getCreatedAt();
        this.updatedAt = taskColumn.getUpdatedAt();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}