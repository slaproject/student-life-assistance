package com.studentapp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class TaxSection {
    private String title;
    private List<TaxSubsection> subsections;
    private List<String> items;

    public TaxSection() {
        this.subsections = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public TaxSection(String title) {
        this.title = title;
        this.subsections = new ArrayList<>();
        this.items = new ArrayList<>();
    }
}

