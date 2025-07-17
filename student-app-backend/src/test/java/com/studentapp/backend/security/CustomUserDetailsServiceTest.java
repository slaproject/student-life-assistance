package com.studentapp.backend.security;

import com.studentapp.backend.repository.UserRepository;
import com.studentapp.common.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomUserDetailsService, covering user loading logic.
 */
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifies loadUserByUsername returns UserDetails when user is found.
     */
    @Test
    void loadUserByUsernameUserFoundReturnsUserDetails() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        UserDetails details = service.loadUserByUsername("testuser");
        assertThat(details.getUsername()).isEqualTo("testuser");
        assertThat(details.getPassword()).isEqualTo("password");
        assertThat(details.getAuthorities()).extracting("authority").containsExactly("USER");
    }

    /**
     * Verifies loadUserByUsername throws when user is not found.
     */
    @Test
    void loadUserByUsernameUserNotFoundThrows() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.loadUserByUsername("nouser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with username: nouser");
    }
} 