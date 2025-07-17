package com.studentapp.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SecurityConfig, covering bean creation logic.
 */
class SecurityConfigTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpSecurity httpSecurity;

    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifies passwordEncoder bean returns a BCryptPasswordEncoder.
     */
    @Test
    void passwordEncoderReturnsBCryptPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertThat(encoder).isNotNull();
        assertThat(encoder.getClass().getSimpleName()).isEqualTo("BCryptPasswordEncoder");
    }

    /**
     * Verifies filterChain bean can be created without exception.
     */
    @Test
    void filterChainBeanCanBeCreated() throws Exception {
        // Since HttpSecurity is complex, just verify the bean is created without exception
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        when(http.csrf(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        when(http.build()).thenReturn(mock(DefaultSecurityFilterChain.class));
        SecurityFilterChain chain = securityConfig.filterChain(http);
        assertThat(chain).isNotNull();
    }
} 