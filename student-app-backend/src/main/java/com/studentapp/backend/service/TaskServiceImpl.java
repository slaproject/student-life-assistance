package com.studentapp.backend.service;

import com.studentapp.backend.repository.*;
import com.studentapp.common.model.*;
import com.studentapp.common.model.Task.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskColumnRepository columnRepository;

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    // Task Column operations
    @Override
    @Transactional
    public TaskColumn saveColumn(TaskColumn column, UUID userId) {
        column.setUserId(userId);
        
        if (column.getId() == null) {
            // Creating new column - handle position management
            if (column.getPosition() == null) {
                // No position specified, add at end
                Integer maxPosition = columnRepository.findMaxPositionByUserId(userId);
                column.setPosition(maxPosition != null ? maxPosition + 1 : 0);
            } else {
                // Position specified, shift existing columns
                Integer targetPosition = column.getPosition();
                
                // Ensure position is non-negative
                if (targetPosition < 0) {
                    targetPosition = 0;
                    column.setPosition(targetPosition);
                }
                
                // Increment positions of all columns at and after the target position
                columnRepository.incrementPositionsFrom(userId, targetPosition);
            }
        } else {
            // Updating existing column - for now, keep simple update
            // TODO: Handle position changes for updates if needed
        }
        
        return columnRepository.save(column);
    }

    @Override
    public Optional<TaskColumn> getColumnById(UUID id, UUID userId) {
        return columnRepository.findById(id)
            .filter(column -> column.getUserId().equals(userId));
    }

    @Override
    public List<TaskColumn> getAllColumns(UUID userId) {
        return columnRepository.findByUserIdOrderByPositionAsc(userId);
    }

    @Override
    public void deleteColumn(UUID id, UUID userId) {
        Optional<TaskColumn> columnOpt = getColumnById(id, userId);
        if (columnOpt.isPresent()) {
            TaskColumn column = columnOpt.get();
            
            // Move all tasks from this column to the first available column
            List<TaskColumn> otherColumns = columnRepository.findByUserIdOrderByPositionAsc(userId)
                .stream()
                .filter(c -> !c.getId().equals(id))
                .collect(Collectors.toList());
            
            if (!otherColumns.isEmpty()) {
                TaskColumn targetColumn = otherColumns.get(0);
                List<Task> tasksToMove = taskRepository.findByUserIdAndColumn_IdOrderByPositionAsc(userId, id);
                for (Task task : tasksToMove) {
                    task.setColumn(targetColumn);
                    taskRepository.save(task);
                }
            }
            
            columnRepository.delete(column);
        }
    }

    @Override
    public TaskColumn updateColumnPosition(UUID id, Integer newPosition, UUID userId) {
        Optional<TaskColumn> columnOpt = getColumnById(id, userId);
        if (columnOpt.isPresent()) {
            TaskColumn column = columnOpt.get();
            column.setPosition(newPosition);
            return columnRepository.save(column);
        }
        throw new RuntimeException("Column not found");
    }

    @Override
    public void initializeDefaultColumns(UUID userId) {
        if (columnRepository.countByUserId(userId) == 0) {
            TaskColumn todoColumn = new TaskColumn(userId, "To Do", "#e3f2fd", 0);
            TaskColumn inProgressColumn = new TaskColumn(userId, "In Progress", "#fff3e0", 1);
            TaskColumn doneColumn = new TaskColumn(userId, "Done", "#e8f5e8", 2);
            
            columnRepository.save(todoColumn);
            columnRepository.save(inProgressColumn);
            columnRepository.save(doneColumn);
        }
    }

    // Task operations
    @Override
    public Task saveTask(Task task, UUID userId) {
        task.setUserId(userId);
        if (task.getPosition() == null && task.getColumn() != null) {
            Integer maxPosition = taskRepository.findMaxPositionByUserIdAndColumnId(userId, task.getColumn().getId());
            task.setPosition(maxPosition != null ? maxPosition + 1 : 0);
        }
        return taskRepository.save(task);
    }

    @Override
    public Optional<Task> getTaskById(UUID id, UUID userId) {
        return taskRepository.findById(id)
            .filter(task -> task.getUserId().equals(userId));
    }

    @Override
    public List<Task> getAllTasks(UUID userId) {
        return taskRepository.findByUserIdOrderByPositionAsc(userId);
    }

    @Override
    public List<Task> getTasksByColumn(UUID userId, UUID columnId) {
        return taskRepository.findByUserIdAndColumn_IdOrderByPositionAsc(userId, columnId);
    }

    @Override
    public List<Task> getTasksByPriority(UUID userId, Priority priority) {
        return taskRepository.findByUserIdAndPriorityOrderByCreatedAtDesc(userId, priority);
    }

    @Override
    public List<Task> getUpcomingTasks(UUID userId, int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(days);
        return taskRepository.findUpcomingTasksByUserId(userId, now, endDate);
    }

    @Override
    public List<Task> getOverdueTasks(UUID userId) {
        return taskRepository.findOverdueTasksByUserId(userId, LocalDateTime.now());
    }

    @Override
    public List<Task> searchTasks(UUID userId, String searchTerm) {
        return taskRepository.searchTasksByUserId(userId, searchTerm);
    }

    @Override
    public void deleteTask(UUID id, UUID userId) {
        Optional<Task> taskOpt = getTaskById(id, userId);
        if (taskOpt.isPresent()) {
            taskRepository.delete(taskOpt.get());
        }
    }

    @Override
    public Task updateTaskPosition(UUID taskId, UUID newColumnId, Integer newPosition, UUID userId) {
        Optional<Task> taskOpt = getTaskById(taskId, userId);
        Optional<TaskColumn> columnOpt = getColumnById(newColumnId, userId);
        
        if (taskOpt.isPresent() && columnOpt.isPresent()) {
            Task task = taskOpt.get();
            TaskColumn newColumn = columnOpt.get();
            
            task.setColumn(newColumn);
            task.setPosition(newPosition);
            
            return taskRepository.save(task);
        }
        throw new RuntimeException("Task or column not found");
    }

    @Override
    public Task moveTaskToColumn(UUID taskId, UUID columnId, UUID userId) {
        Optional<Task> taskOpt = getTaskById(taskId, userId);
        Optional<TaskColumn> columnOpt = getColumnById(columnId, userId);
        
        if (taskOpt.isPresent() && columnOpt.isPresent()) {
            Task task = taskOpt.get();
            TaskColumn newColumn = columnOpt.get();
            
            // Set position to end of the new column
            Integer maxPosition = taskRepository.findMaxPositionByUserIdAndColumnId(userId, columnId);
            task.setColumn(newColumn);
            task.setPosition(maxPosition != null ? maxPosition + 1 : 0);
            
            return taskRepository.save(task);
        }
        throw new RuntimeException("Task or column not found");
    }

    // Task Attachment operations
    @Override
    public TaskAttachment saveAttachment(TaskAttachment attachment, UUID userId) {
        // Verify task belongs to user
        if (getTaskById(attachment.getTask().getId(), userId).isEmpty()) {
            throw new RuntimeException("Task not found or access denied");
        }
        return attachmentRepository.save(attachment);
    }

    @Override
    public Optional<TaskAttachment> getAttachmentById(UUID id, UUID userId) {
        return attachmentRepository.findById(id)
            .filter(attachment -> attachment.getTask().getUserId().equals(userId));
    }

    @Override
    public List<TaskAttachment> getAttachmentsByTask(UUID taskId, UUID userId) {
        // Verify task belongs to user
        if (getTaskById(taskId, userId).isEmpty()) {
            throw new RuntimeException("Task not found or access denied");
        }
        return attachmentRepository.findByTaskIdOrderByUploadedAtDesc(taskId);
    }

    @Override
    public void deleteAttachment(UUID id, UUID userId) {
        Optional<TaskAttachment> attachmentOpt = getAttachmentById(id, userId);
        if (attachmentOpt.isPresent()) {
            attachmentRepository.delete(attachmentOpt.get());
        }
    }

    // Analytics and reporting
    @Override
    public Map<String, Long> getTaskStatistics(UUID userId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalTasks", taskRepository.countByUserId(userId));
        stats.put("completedTasks", taskRepository.countCompletedTasksByUserId(userId));
        stats.put("overdueTasks", taskRepository.countOverdueTasksByUserId(userId, LocalDateTime.now()));
        return stats;
    }

    @Override
    public Map<Priority, Long> getTaskCountByPriority(UUID userId) {
        List<Object[]> results = taskRepository.getTaskCountByPriority(userId);
        return results.stream()
            .collect(Collectors.toMap(
                result -> (Priority) result[0],
                result -> (Long) result[1]
            ));
    }

    @Override
    public Map<String, Long> getTaskCountByStatus(UUID userId) {
        List<Object[]> results = taskRepository.getTaskCountByStatus(userId);
        return results.stream()
            .collect(Collectors.toMap(
                result -> (String) result[0],
                result -> (Long) result[1]
            ));
    }

    @Override
    public List<Task> getTasksDueSoon(UUID userId, int days) {
        return getUpcomingTasks(userId, days);
    }

    // Bulk operations
    @Override
    public void moveMultipleTasks(List<UUID> taskIds, UUID targetColumnId, UUID userId) {
        Optional<TaskColumn> columnOpt = getColumnById(targetColumnId, userId);
        if (columnOpt.isEmpty()) {
            throw new RuntimeException("Target column not found");
        }
        
        TaskColumn targetColumn = columnOpt.get();
        Integer maxPosition = taskRepository.findMaxPositionByUserIdAndColumnId(userId, targetColumnId);
        int startPosition = maxPosition != null ? maxPosition + 1 : 0;
        
        for (int i = 0; i < taskIds.size(); i++) {
            UUID taskId = taskIds.get(i);
            Optional<Task> taskOpt = getTaskById(taskId, userId);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                task.setColumn(targetColumn);
                task.setPosition(startPosition + i);
                taskRepository.save(task);
            }
        }
    }

    @Override
    public void deleteMultipleTasks(List<UUID> taskIds, UUID userId) {
        for (UUID taskId : taskIds) {
            deleteTask(taskId, userId);
        }
    }

    @Override
    public void updateMultipleTasksPriority(List<UUID> taskIds, Priority priority, UUID userId) {
        for (UUID taskId : taskIds) {
            Optional<Task> taskOpt = getTaskById(taskId, userId);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                task.setPriority(priority);
                taskRepository.save(task);
            }
        }
    }
}