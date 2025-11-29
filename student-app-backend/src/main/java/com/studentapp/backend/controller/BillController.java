package com.studentapp.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentapp.backend.service.BillExtractionService;
import com.studentapp.backend.service.FinanceService;
import com.studentapp.backend.repository.UserRepository;
import com.studentapp.common.model.Expense;
import com.studentapp.common.model.ExpenseCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class BillController {

    private final BillExtractionService billExtractionService;
    private final FinanceService financeService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    private UUID resolveUserId(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .map(com.studentapp.common.model.User::getId)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadBill(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            UUID userId = resolveUserId(authentication);
            
            // Get available categories
            List<ExpenseCategory> categories = financeService.getActiveCategories(userId);
            List<String> categoryNames = categories.stream()
                    .map(ExpenseCategory::getName)
                    .collect(Collectors.toList());

            // Extract details
            String jsonResponse = billExtractionService.extractBillDetails(
                    file.getBytes(),
                    file.getContentType(),
                    categoryNames
            );

            log.info("Extracted bill details: {}", jsonResponse);

            // Parse JSON
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            String dateStr = rootNode.path("date").asText();
            BigDecimal amount = new BigDecimal(rootNode.path("amount").asText());
            String categoryName = rootNode.path("category").asText();

            // Find category
            ExpenseCategory category = categories.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                    .findFirst()
                    .orElseGet(() -> {
                        // Fallback: try to find "Other" or just use the first one
                        return categories.stream()
                                .filter(c -> c.getName().equalsIgnoreCase("Other"))
                                .findFirst()
                                .orElse(categories.isEmpty() ? null : categories.get(0));
                    });

            if (category == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No valid category found.");
            }

            // Create Expense
            Expense expense = new Expense();
            expense.setTitle("Bill Upload: " + categoryName);
            expense.setAmount(amount);
            expense.setExpenseDate(LocalDate.parse(dateStr)); // Parse String to LocalDate
            expense.setCategory(category);
            expense.setDescription("Automatically extracted from bill upload");
            expense.setPaymentMethod("card"); // Default

            Expense savedExpense = financeService.saveExpense(expense, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);

        } catch (Exception e) {
            log.error("Error processing bill upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing bill: " + e.getMessage());
        }
    }
}
