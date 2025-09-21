package com.studentapp.backend.controller;

import com.studentapp.backend.service.TaskService;
import com.studentapp.backend.repository.UserRepository;
import com.studentapp.backend.dto.TaskColumnDto;
import com.studentapp.backend.dto.TaskDto;
import com.studentapp.common.model.*;
import com.studentapp.common.model.Task.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    private UUID resolveUserId(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .map(com.studentapp.common.model.User::getId)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    // Task Column endpoints
    @GetMapping("/columns")
    public ResponseEntity<List<TaskColumnDto>> getAllColumns(Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        List<TaskColumn> columns = taskService.getAllColumns(userId);
        List<TaskColumnDto> columnDtos = columns.stream()
            .map(TaskColumnDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(columnDtos);
    }

    @GetMapping("/columns/{id}")
    public ResponseEntity<TaskColumn> getColumnById(@PathVariable("id") UUID id, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        Optional<TaskColumn> column = taskService.getColumnById(id, userId);
        return column.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/columns")
    public ResponseEntity<TaskColumn> createColumn(@RequestBody TaskColumn column, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        TaskColumn savedColumn = taskService.saveColumn(column, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedColumn);
    }

    @PutMapping("/columns/{id}")
    public ResponseEntity<TaskColumn> updateColumn(@PathVariable("id") UUID id, @RequestBody TaskColumn column, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        Optional<TaskColumn> existingColumn = taskService.getColumnById(id, userId);
        if (existingColumn.isPresent()) {
            column.setId(id);
            TaskColumn updatedColumn = taskService.saveColumn(column, userId);
            return ResponseEntity.ok(updatedColumn);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/columns/{id}/position")
    public ResponseEntity<TaskColumn> updateColumnPosition(@PathVariable("id") UUID id, @RequestBody Map<String, Integer> positionData, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        Integer newPosition = positionData.get("position");
        try {
            TaskColumn updatedColumn = taskService.updateColumnPosition(id, newPosition, userId);
            return ResponseEntity.ok(updatedColumn);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/columns/{id}")
    public ResponseEntity<Void> deleteColumn(@PathVariable("id") UUID id, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        taskService.deleteColumn(id, userId);
        return ResponseEntity.noContent().build();
    }

    // Task endpoints
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        List<Task> tasks = taskService.getAllTasks(userId);
        List<TaskDto> taskDtos = tasks.stream()
            .map(TaskDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(taskDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable("id") UUID id, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        Optional<Task> task = taskService.getTaskById(id, userId);
        return task.map(t -> ResponseEntity.ok(new TaskDto(t)))
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/column/{columnId}")
    public ResponseEntity<List<TaskDto>> getTasksByColumn(@PathVariable("columnId") UUID columnId, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        List<Task> tasks = taskService.getTasksByColumn(userId, columnId);
        List<TaskDto> taskDtos = tasks.stream()
            .map(TaskDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(taskDtos);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskDto>> getTasksByPriority(@PathVariable("priority") Priority priority, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        List<Task> tasks = taskService.getTasksByPriority(userId, priority);
        List<TaskDto> taskDtos = tasks.stream()
            .map(TaskDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(taskDtos);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<TaskDto>> getUpcomingTasks(@RequestParam(defaultValue = "7") int days, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        List<Task> tasks = taskService.getUpcomingTasks(userId, days);
        List<TaskDto> taskDtos = tasks.stream()
            .map(TaskDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(taskDtos);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDto>> getOverdueTasks(Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        List<Task> tasks = taskService.getOverdueTasks(userId);
        List<TaskDto> taskDtos = tasks.stream()
            .map(TaskDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(taskDtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskDto>> searchTasks(@RequestParam String query, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        List<Task> tasks = taskService.searchTasks(userId, query);
        List<TaskDto> taskDtos = tasks.stream()
            .map(TaskDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(taskDtos);
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody Task task, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        Task savedTask = taskService.saveTask(task, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TaskDto(savedTask));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable("id") UUID id, @RequestBody Task task, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        Optional<Task> existingTask = taskService.getTaskById(id, userId);
        if (existingTask.isPresent()) {
            task.setId(id);
            Task updatedTask = taskService.saveTask(task, userId);
            return ResponseEntity.ok(new TaskDto(updatedTask));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/position")
    public ResponseEntity<TaskDto> updateTaskPosition(@PathVariable("id") UUID id, @RequestBody Map<String, Object> positionData, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        UUID newColumnId = UUID.fromString((String) positionData.get("columnId"));
        Integer newPosition = (Integer) positionData.get("position");
        try {
            Task updatedTask = taskService.updateTaskPosition(id, newColumnId, newPosition, userId);
            return ResponseEntity.ok(new TaskDto(updatedTask));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/move/{columnId}")
    public ResponseEntity<TaskDto> moveTaskToColumn(@PathVariable("id") UUID id, @PathVariable("columnId") UUID columnId, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        try {
            Task updatedTask = taskService.moveTaskToColumn(id, columnId, userId);
            return ResponseEntity.ok(new TaskDto(updatedTask));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") UUID id, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }

    // Bulk operations
    @PostMapping("/bulk/move")
    public ResponseEntity<Void> moveMultipleTasks(@RequestBody Map<String, Object> bulkData, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        @SuppressWarnings("unchecked")
        List<String> taskIdStrings = (List<String>) bulkData.get("taskIds");
        List<UUID> taskIds = taskIdStrings.stream().map(UUID::fromString).toList();
        UUID targetColumnId = UUID.fromString((String) bulkData.get("targetColumnId"));
        taskService.moveMultipleTasks(taskIds, targetColumnId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk/delete")
    public ResponseEntity<Void> deleteMultipleTasks(@RequestBody Map<String, List<String>> bulkData, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        List<String> taskIdStrings = bulkData.get("taskIds");
        List<UUID> taskIds = taskIdStrings.stream().map(UUID::fromString).toList();
        taskService.deleteMultipleTasks(taskIds, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk/priority")
    public ResponseEntity<Void> updateMultipleTasksPriority(@RequestBody Map<String, Object> bulkData, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        @SuppressWarnings("unchecked")
        List<String> taskIdStrings = (List<String>) bulkData.get("taskIds");
        List<UUID> taskIds = taskIdStrings.stream().map(UUID::fromString).toList();
        Priority priority = Priority.valueOf((String) bulkData.get("priority"));
        taskService.updateMultipleTasksPriority(taskIds, priority, userId);
        return ResponseEntity.ok().build();
    }

    // Task Attachment endpoints
    @GetMapping("/{taskId}/attachments")
    public ResponseEntity<List<TaskAttachment>> getTaskAttachments(@PathVariable("taskId") UUID taskId, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        try {
            List<TaskAttachment> attachments = taskService.getAttachmentsByTask(taskId, userId);
            return ResponseEntity.ok(attachments);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{taskId}/attachments")
    public ResponseEntity<TaskAttachment> addTaskAttachment(@PathVariable("taskId") UUID taskId, @RequestBody TaskAttachment attachment, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        try {
            Optional<Task> taskOpt = taskService.getTaskById(taskId, userId);
            if (taskOpt.isPresent()) {
                attachment.setTask(taskOpt.get());
                TaskAttachment savedAttachment = taskService.saveAttachment(attachment, userId);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedAttachment);
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteTaskAttachment(@PathVariable("attachmentId") UUID attachmentId, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        taskService.deleteAttachment(attachmentId, userId);
        return ResponseEntity.noContent().build();
    }

    // Analytics endpoints
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getTaskStatistics(Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        Map<String, Long> stats = taskService.getTaskStatistics(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/priority")
    public ResponseEntity<Map<Priority, Long>> getTaskCountByPriority(Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        Map<Priority, Long> stats = taskService.getTaskCountByPriority(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/status")
    public ResponseEntity<Map<String, Long>> getTaskCountByStatus(Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        Map<String, Long> stats = taskService.getTaskCountByStatus(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/due-soon")
    public ResponseEntity<List<Task>> getTasksDueSoon(@RequestParam(defaultValue = "3") int days, Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        List<Task> tasks = taskService.getTasksDueSoon(userId, days);
        return ResponseEntity.ok(tasks);
    }
}