package com.studentapp.backend.controller;

import com.studentapp.backend.dto.SummaryRequest;
import com.studentapp.backend.dto.SummaryResponse;
import com.studentapp.backend.service.RateLimitService;
import com.studentapp.backend.service.SummaryService;
import com.studentapp.common.model.User;
import com.studentapp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    private final SummaryService summaryService;
    private final RateLimitService rateLimitService;
    private final UserRepository userRepository;

    public SummaryController(SummaryService summaryService, RateLimitService rateLimitService, UserRepository userRepository) {
        this.summaryService = summaryService;
        this.rateLimitService = rateLimitService;
        this.userRepository = userRepository;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateSummary(@RequestBody SummaryRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!rateLimitService.checkAndIncrement(user)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Monthly limit of 5 requests reached.");
        }

        try {
            String summary = summaryService.generateSummary(request, user);
            int remaining = rateLimitService.getRemainingRequests(user);
            return ResponseEntity.ok(new SummaryResponse(summary, remaining));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/usage")
    public ResponseEntity<?> getUsage(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        int remaining = rateLimitService.getRemainingRequests(user);
        return ResponseEntity.ok(remaining);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok(summaryService.getUserHistory(user));
    }
}
