package com.studentapp.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@JsonIgnoreProperties({"tasks"})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id", nullable = false)
    @JsonIgnoreProperties("tasks")
    private TaskColumn column;

    @Column(name = "column_id", insertable = false, updatable = false)
    private UUID columnId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "assigned_to")
    private UUID assignedTo;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "position")
    private Integer position = 0;

    @Column(name = "tags", length = 1000)
    private String tags; // comma-separated tags

    @Column(name = "project_id")
    private UUID projectId; // for future project grouping

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TaskAttachment> attachments;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public Task() {}

    public Task(UUID userId, TaskColumn column, String title, Priority priority) {
        this.userId = userId;
        this.column = column;
        this.title = title;
        this.priority = priority;
        this.position = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public TaskColumn getColumn() { return column; }
    public void setColumn(TaskColumn column) { this.column = column; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public UUID getAssignedTo() { return assignedTo; }
    public void setAssignedTo(UUID assignedTo) { this.assignedTo = assignedTo; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<TaskAttachment> getAttachments() { return attachments; }
    public void setAttachments(List<TaskAttachment> attachments) { this.attachments = attachments; }
    
    // Helper method for compatibility with service layer
    public UUID getColumnId() {
        return columnId != null ? columnId : (column != null ? column.getId() : null);
    }
    
    public void setColumnId(UUID columnId) {
        this.columnId = columnId;
        // Note: The actual column reference should be set separately for proper JPA relationships
    }
}