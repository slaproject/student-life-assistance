package com.studentapp.backend.controller;

import com.studentapp.backend.dto.TaxAgentRequest;
import com.studentapp.backend.dto.TaxAgentResponse;
import com.studentapp.backend.service.TaxAgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tax-agent")
public class TaxAgentController {

    private final TaxAgentService taxAgentService;

    public TaxAgentController(TaxAgentService taxAgentService) {
        this.taxAgentService = taxAgentService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateTaxGuidance(
            @RequestBody TaxAgentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            TaxAgentResponse response = taxAgentService.generateTaxGuidance(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while generating tax guidance: " + e.getMessage());
        }
    }
}

