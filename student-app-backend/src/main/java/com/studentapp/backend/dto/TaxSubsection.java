package com.studentapp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaxSubsection {
    private String title;
    private String content;

    public TaxSubsection() {}
}

