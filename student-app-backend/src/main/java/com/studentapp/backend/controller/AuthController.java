package com.studentapp.backend.controller;

import com.studentapp.common.model.User;
import com.studentapp.backend.repository.UserRepository;
import com.studentapp.backend.security.JwtUtil;
import com.studentapp.backend.service.CategoryInitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CategoryInitializationService categoryInitializationService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public String registerUser(@RequestBody User user) {
        try {
            // Check if username already exists
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                return "Username already exists";
            }
            
            // Check if email already exists
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                return "Email already exists";
            }
            
            // Encode password and save user
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            
            // Create default categories for the new user
            categoryInitializationService.createDefaultCategoriesForUser(savedUser.getId());
            
            return "User registered successfully with default categories";
            
        } catch (Exception e) {
            // Log the error (you should use proper logging in production)
            System.err.println("Error during user registration: " + e.getMessage());
            return "Registration failed. Please try again.";
        }
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody User loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user);
                return token;
            }
        }
        return "Invalid username or password";
    }
}
