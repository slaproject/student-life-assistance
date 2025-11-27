package com.studentapp.backend.service;

import com.studentapp.backend.repository.ExpenseCategoryRepository;
import com.studentapp.backend.repository.UserRepository;
import com.studentapp.common.model.ExpenseCategory;
import com.studentapp.common.model.User;
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

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateDefaultCategoriesForUser() {
        // Given - create a real user in the database
        User user = new User();
        user.setUsername("testuser_" + UUID.randomUUID());
        user.setEmail("test_" + UUID.randomUUID() + "@example.com");
        user.setPassword("password");
        User savedUser = userRepository.save(user);
        UUID userId = savedUser.getId();
        
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
