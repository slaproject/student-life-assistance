package com.studentapp.backend.service;

import com.studentapp.common.model.TaskColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TaskInitializationService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskInitializationService.class);
    
    @Autowired
    private TaskService taskService;

    @Value("${app.default-task-columns.enabled:true}")
    private boolean defaultTaskColumnsEnabled;
    
    /**
     * Create default task columns for new users
     */
    public void createDefaultTaskColumnsForUser(UUID userId) {
        // Check if default task columns are enabled
        if (!defaultTaskColumnsEnabled) {
            logger.info("Default task columns are disabled, skipping creation for user {}", userId);
            return;
        }
        
        // Use the existing service method that already checks if columns exist
        try {
            taskService.initializeDefaultColumns(userId);
            logger.info("Successfully initialized default task columns for user {}", userId);
        } catch (Exception e) {
            logger.error("Failed to create default task columns for user {}: {}", userId, e.getMessage());
        }
    }
    
    /**
     * Alternative method to create default columns with custom templates
     */
    public void createCustomDefaultTaskColumnsForUser(UUID userId) {
        if (!defaultTaskColumnsEnabled) {
            logger.info("Default task columns are disabled, skipping creation for user {}", userId);
            return;
        }
        
        List<DefaultTaskColumnTemplate> templates = getDefaultTaskColumnTemplates();
        
        logger.info("Creating {} default task columns for user {}", templates.size(), userId);
        
        int successCount = 0;
        for (DefaultTaskColumnTemplate template : templates) {
            try {
                TaskColumn column = new TaskColumn();
                column.setUserId(userId);
                column.setTitle(template.getTitle());
                column.setColor(template.getColor());
                column.setPosition(template.getPosition());
                
                taskService.saveColumn(column, userId);
                successCount++;
                
            } catch (Exception e) {
                logger.error("Failed to create default task column '{}' for user {}: {}", 
                           template.getTitle(), userId, e.getMessage());
            }
        }
        
        logger.info("Successfully created {} out of {} default task columns for user {}", 
                   successCount, templates.size(), userId);
    }
    
    private List<DefaultTaskColumnTemplate> getDefaultTaskColumnTemplates() {
        return Arrays.asList(
            new DefaultTaskColumnTemplate("To Do", "#e3f2fd", 0),
            new DefaultTaskColumnTemplate("In Progress", "#fff3e0", 1),
            new DefaultTaskColumnTemplate("Review", "#f3e5f5", 2),
            new DefaultTaskColumnTemplate("Done", "#e8f5e8", 3)
        );
    }
    
    /**
     * Get count of default task columns that would be created
     */
    public int getDefaultTaskColumnCount() {
        return defaultTaskColumnsEnabled ? 3 : 0; // To Do, In Progress, Done
    }
    
    /**
     * Check if user has any task columns
     */
    public boolean hasTaskColumns(UUID userId) {
        List<TaskColumn> columns = taskService.getAllColumns(userId);
        return !columns.isEmpty();
    }
    
    /**
     * Helper class for task column templates
     */
    private static class DefaultTaskColumnTemplate {
        private final String title;
        private final String color;
        private final Integer position;
        
        public DefaultTaskColumnTemplate(String title, String color, Integer position) {
            this.title = title;
            this.color = color;
            this.position = position;
        }
        
        public String getTitle() { return title; }
        public String getColor() { return color; }
        public Integer getPosition() { return position; }
    }
}