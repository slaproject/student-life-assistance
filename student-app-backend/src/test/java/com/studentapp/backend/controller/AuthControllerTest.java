package com.studentapp.backend.controller;

import com.studentapp.backend.repository.UserRepository;
import com.studentapp.backend.security.JwtUtil;
import com.studentapp.common.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthController, covering registration and login scenarios.
 */
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifies registration fails if the username already exists.
     */
    @Test
    void registerUserWhenUsernameExistsReturnsUsernameExists() {
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User()));
        String result = authController.registerUser(user);
        assertThat(result).isEqualTo("Username already exists");
    }

    /**
     * Verifies registration fails if the email already exists.
     */
    @Test
    void registerUserWhenEmailExistsReturnsEmailExists() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
        String result = authController.registerUser(user);
        assertThat(result).isEqualTo("Email already exists");
    }

    /**
     * Verifies successful user registration with valid data.
     */
    @Test
    void registerUserWhenValidReturnsSuccess() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        String result = authController.registerUser(user);
        assertThat(result).isEqualTo("User registered successfully");
        verify(userRepository).save(any(User.class));
    }

    /**
     * Verifies login returns a token for valid credentials.
     */
    @Test
    void loginUserWhenValidCredentialsReturnsToken() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(new BCryptPasswordEncoder().encode("password"));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("token123");
        User loginRequest = new User();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");
        String result = authController.loginUser(loginRequest);
        assertThat(result).isEqualTo("token123");
    }

    /**
     * Verifies login fails for invalid username.
     */
    @Test
    void loginUserWhenInvalidUsernameReturnsInvalid() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        User loginRequest = new User();
        loginRequest.setUsername("nouser");
        loginRequest.setPassword("password");
        String result = authController.loginUser(loginRequest);
        assertThat(result).isEqualTo("Invalid username or password");
    }

    /**
     * Verifies login fails for invalid password.
     */
    @Test
    void loginUserWhenInvalidPasswordReturnsInvalid() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(new BCryptPasswordEncoder().encode("password"));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        User loginRequest = new User();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");
        String result = authController.loginUser(loginRequest);
        assertThat(result).isEqualTo("Invalid username or password");
    }

    /**
     * Verifies registerUser throws NullPointerException for null user.
     */
    @Test
    void registerUserWithNullUserThrows() {
        assertThatThrownBy(() -> authController.registerUser(null)).isInstanceOf(NullPointerException.class);
    }

    /**
     * Verifies registerUser returns username exists for null username.
     */
    @Test
    void registerUserWithNullUsernameReturnsUsernameExists() {
        User user = new User();
        user.setUsername(null);
        when(userRepository.findByUsername(null)).thenReturn(Optional.of(new User()));
        String result = authController.registerUser(user);
        assertThat(result).isEqualTo("Username already exists");
    }

    /**
     * Verifies registerUser returns email exists for null email.
     */
    @Test
    void registerUserWithNullEmailReturnsEmailExists() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail(null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail(null)).thenReturn(Optional.of(new User()));
        String result = authController.registerUser(user);
        assertThat(result).isEqualTo("Email already exists");
    }

    /**
     * Verifies registerUser throws IllegalArgumentException for null password.
     */
    @Test
    void registerUserWithNullPasswordThrows() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authController.registerUser(user)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Verifies loginUser throws NullPointerException for null login request.
     */
    @Test
    void loginUserWithNullLoginRequestThrows() {
        assertThatThrownBy(() -> authController.loginUser(null)).isInstanceOf(NullPointerException.class);
    }

    /**
     * Verifies loginUser returns invalid for null username.
     */
    @Test
    void loginUserWithNullUsernameReturnsInvalid() {
        User loginRequest = new User();
        loginRequest.setUsername(null);
        loginRequest.setPassword("password");
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());
        String result = authController.loginUser(loginRequest);
        assertThat(result).isEqualTo("Invalid username or password");
    }

    /**
     * Verifies loginUser throws IllegalArgumentException for null password.
     */
    @Test
    void loginUserWithNullPasswordReturnsInvalid() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(new BCryptPasswordEncoder().encode("password"));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        User loginRequest = new User();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword(null);
        assertThatThrownBy(() -> authController.loginUser(loginRequest)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Verifies loginUser returns invalid if DB user has null password.
     */
    @Test
    void loginUserWithDbUserNullPasswordReturnsInvalid() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        User loginRequest = new User();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");
        String result = authController.loginUser(loginRequest);
        assertThat(result).isEqualTo("Invalid username or password");
    }

    /**
     * Verifies loginUser returns invalid if DB user has empty password.
     */
    @Test
    void loginUserWithDbUserEmptyPasswordReturnsInvalid() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        User loginRequest = new User();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");
        String result = authController.loginUser(loginRequest);
        assertThat(result).isEqualTo("Invalid username or password");
    }
} 