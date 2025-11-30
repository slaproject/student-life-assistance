package com.studentapp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class TaxAgentResponse {
    private List<TaxSection> sections;
    private List<String> links;
    private String summary;

    public TaxAgentResponse() {}

    public List<TaxSection> getSections() {
        return sections;
    }

    public void setSections(List<TaxSection> sections) {
        this.sections = sections;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}

