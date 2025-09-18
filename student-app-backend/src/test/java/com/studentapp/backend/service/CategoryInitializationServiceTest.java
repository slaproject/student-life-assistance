package com.studentapp.backend.service;

import com.studentapp.backend.repository.ExpenseCategoryRepository;
import com.studentapp.common.model.ExpenseCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"app.default-categories.enabled=true"})
@Transactional
class CategoryInitializationServiceTest {

    @Autowired
    private CategoryInitializationService categoryInitializationService;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Test
    void testCreateDefaultCategoriesForUser() {
        // Given
        UUID userId = UUID.randomUUID();
        
        // When
        categoryInitializationService.createDefaultCategoriesForUser(userId);
        
        // Then
        List<ExpenseCategory> userCategories = expenseCategoryRepository.findByUserId(userId);
        assertFalse(userCategories.isEmpty());
        assertTrue(userCategories.size() >= 5); // Should have at least 5 default categories
        
        // Verify specific categories exist
        boolean hasFoodCategory = userCategories.stream()
            .anyMatch(cat -> cat.getName().equals("Food & Dining"));
        assertTrue(hasFoodCategory);
        
        boolean hasTransportationCategory = userCategories.stream()
            .anyMatch(cat -> cat.getName().equals("Transportation"));
        assertTrue(hasTransportationCategory);
        
        // Verify all categories belong to the user and are active
        userCategories.forEach(category -> {
            assertEquals(userId, category.getUserId());
            assertTrue(category.getIsActive());
            assertNotNull(category.getName());
            assertNotNull(category.getColor());
            assertNotNull(category.getIcon());
        });
    }
    
    @Test
    void testGetDefaultCategoryCount() {
        // When
        int count = categoryInitializationService.getDefaultCategoryCount();
        
        // Then
        assertTrue(count > 0);
        assertEquals(10, count); // We have 10 default categories defined
    }
}
