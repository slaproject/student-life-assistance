package com.studentapp.backend.repository;

import com.studentapp.common.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserRepository, verifying custom method mocks.
 */
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifies findByUsername can be called on a mock.
     */
    @Test
    void findByUsernameCanBeCalled() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        Optional<User> result = userRepository.findByUsername("testuser");
        assertThat(result).isNotNull();
    }

    /**
     * Verifies findByEmail can be called on a mock.
     */
    @Test
    void findByEmailCanBeCalled() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        Optional<User> result = userRepository.findByEmail("test@example.com");
        assertThat(result).isNotNull();
    }
} 