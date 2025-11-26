package com.studentapp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SummaryResponse {
    private String summary;
    private int remainingRequests;

    public SummaryResponse() {}

    public SummaryResponse(String summary, int remainingRequests) {
        this.summary = summary;
        this.remainingRequests = remainingRequests;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getRemainingRequests() {
        return remainingRequests;
    }

    public void setRemainingRequests(int remainingRequests) {
        this.remainingRequests = remainingRequests;
    }
}
