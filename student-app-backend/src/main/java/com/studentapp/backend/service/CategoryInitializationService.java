package com.studentapp.backend.service;

import com.studentapp.backend.service.FinanceService;
import com.studentapp.common.model.ExpenseCategory;
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
public class CategoryInitializationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryInitializationService.class);
    
    @Autowired
    private FinanceService financeService;

    @Value("${app.default-categories.enabled:true}")
    private boolean defaultCategoriesEnabled;
    
    /**
     * Create default categories for new users
     */
    public void createDefaultCategoriesForUser(UUID userId) {
        // Check if default categories are enabled
        if (!defaultCategoriesEnabled) {
            logger.info("Default categories are disabled, skipping creation for user {}", userId);
            return;
        }
        
        List<DefaultCategoryTemplate> templates = getDefaultCategoryTemplates();
        
        logger.info("Creating {} default categories for user {}", templates.size(), userId);
        
        int successCount = 0;
        for (DefaultCategoryTemplate template : templates) {
            try {
                ExpenseCategory category = new ExpenseCategory();
                category.setUserId(userId);
                category.setName(template.getName());
                category.setDescription(template.getDescription());
                category.setColor(template.getColor());
                category.setIcon(template.getIcon());
                category.setIsActive(true);
                
                financeService.saveCategory(category);
                successCount++;
                
            } catch (Exception e) {
                logger.error("Failed to create default category '{}' for user {}: {}", 
                           template.getName(), userId, e.getMessage());
            }
        }
        
        logger.info("Successfully created {} out of {} default categories for user {}", 
                   successCount, templates.size(), userId);
    }
    
    private List<DefaultCategoryTemplate> getDefaultCategoryTemplates() {
        return Arrays.asList(
            new DefaultCategoryTemplate(
                "Food & Dining", 
                "Restaurants, groceries, takeout, cafeteria meals", 
                "#FF6B6B", 
                "🍽️"
            ),
            new DefaultCategoryTemplate(
                "Transportation", 
                "Bus fare, gas, parking, ride-sharing, vehicle maintenance", 
                "#4ECDC4", 
                "🚌"
            ),
            new DefaultCategoryTemplate(
                "Education", 
                "Books, supplies, course materials, lab fees, tuition", 
                "#96CEB4", 
                "📚"
            ),
            new DefaultCategoryTemplate(
                "Entertainment", 
                "Movies, games, streaming subscriptions, concerts", 
                "#45B7D1", 
                "🎬"
            ),
            new DefaultCategoryTemplate(
                "Shopping", 
                "Clothing, electronics, personal items, accessories", 
                "#BB8FCE", 
                "🛍️"
            ),
            new DefaultCategoryTemplate(
                "Healthcare", 
                "Medical expenses, pharmacy, insurance, wellness", 
                "#F7DC6F", 
                "🏥"
            ),
            new DefaultCategoryTemplate(
                "Utilities", 
                "Internet, phone, electricity bills, subscriptions", 
                "#85C1E9", 
                "💡"
            ),
            new DefaultCategoryTemplate(
                "Housing", 
                "Rent, dormitory fees, maintenance, furniture", 
                "#F8C471", 
                "🏠"
            ),
            new DefaultCategoryTemplate(
                "Personal Care", 
                "Hygiene, grooming, wellness, beauty products", 
                "#D5A6BD", 
                "🧴"
            ),
            new DefaultCategoryTemplate(
                "Miscellaneous", 
                "Other expenses not categorized elsewhere", 
                "#AED6F1", 
                "📋"
            )
        );
    }
    
    /**
     * Get count of default categories that would be created
     */
    public int getDefaultCategoryCount() {
        return defaultCategoriesEnabled ? getDefaultCategoryTemplates().size() : 0;
    }
    
    /**
     * Helper class for category templates
     */
    private static class DefaultCategoryTemplate {
        private final String name;
        private final String description;
        private final String color;
        private final String icon;
        
        public DefaultCategoryTemplate(String name, String description, String color, String icon) {
            this.name = name;
            this.description = description;
            this.color = color;
            this.icon = icon;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getColor() { return color; }
        public String getIcon() { return icon; }
    }
}
