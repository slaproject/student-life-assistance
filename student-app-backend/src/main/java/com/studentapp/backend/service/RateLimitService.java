package com.studentapp.backend.service;

import com.studentapp.backend.model.UsageEntity;
import com.studentapp.backend.repository.UsageRepository;
import com.studentapp.common.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class RateLimitService {

    private final UsageRepository usageRepository;

    public RateLimitService(UsageRepository usageRepository) {
        this.usageRepository = usageRepository;
    }
    private static final int MONTHLY_LIMIT = 5;

    @Transactional
    public boolean checkAndIncrement(User user) {
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        UsageEntity usage = usageRepository.findByUserAndYearMonth(user, currentMonth)
                .orElseGet(() -> new UsageEntity(user, currentMonth, 0));

        if (usage.getRequestCount() >= MONTHLY_LIMIT) {
            return false;
        }

        usage.setRequestCount(usage.getRequestCount() + 1);
        usageRepository.save(usage);
        return true;
    }

    public int getRemainingRequests(User user) {
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return usageRepository.findByUserAndYearMonth(user, currentMonth)
                .map(usage -> Math.max(0, MONTHLY_LIMIT - usage.getRequestCount()))
                .orElse(MONTHLY_LIMIT);
    }
    
    public int getUsedRequests(User user) {
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return usageRepository.findByUserAndYearMonth(user, currentMonth)
                .map(UsageEntity::getRequestCount)
                .orElse(0);
    }
}
