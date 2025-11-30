package com.studentapp.backend.service;

import com.studentapp.backend.dto.TaxAgentRequest;
import com.studentapp.backend.dto.TaxAgentResponse;
import com.studentapp.backend.dto.TaxSection;
import com.studentapp.backend.dto.TaxSubsection;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TaxAgentService {

    private final ChatLanguageModel chatLanguageModel;

    private static final String TAX_GUIDANCE_PROMPT_TEMPLATE =
            "You are a tax advisor helping a student understand their US tax obligations. " +
            "Based on the following information, provide personalized tax guidance:\n\n" +
            "Employment Status:\n" +
            "- On-campus job: {{hasOnCampusJob}}\n" +
            "- Work-study: {{hasWorkStudy}}\n" +
            "- State of residence: {{residentState}}\n" +
            "- State where they work: {{workState}}\n\n" +
            "Please provide:\n" +
            "1. A list of specific steps (as bullet points) the student needs to take for their tax situation\n" +
            "2. Relevant TurboTax links that would help them (format as URLs)\n\n" +
            "Format your response as follows:\n" +
            "STEPS:\n" +
            "- Step 1 description\n" +
            "- Step 2 description\n" +
            "- etc.\n\n" +
            "LINKS:\n" +
            "- https://turbotax.intuit.com/...\n" +
            "- https://turbotax.intuit.com/...\n\n" +
            "Include information about:\n" +
            "- Federal tax obligations for students\n" +
            "- State tax requirements (especially if working in a different state than residence)\n" +
            "- On-campus job tax implications\n" +
            "- Work-study program tax considerations\n" +
            "- Filing requirements and deadlines\n" +
            "- Common deductions and credits available to students\n\n" +
            "Always include these standard TurboTax resources:\n" +
            "- Student Tax Guide: https://turbotax.intuit.com/tax-tips/college-credits-deductions/student-tax-guide/L8K5vVfwJ\n" +
            "- State Tax Information: https://turbotax.intuit.com/tax-tips/state-taxes/\n" +
            "- Work-Study Tax Info: https://turbotax.intuit.com/tax-tips/college-credits-deductions/\n";

    public TaxAgentResponse generateTaxGuidance(TaxAgentRequest request) {
        // Validate input
        if (request.getResidentState() == null || request.getResidentState().trim().isEmpty()) {
            throw new IllegalArgumentException("Resident state is required.");
        }

        // Build prompt
        String hasOnCampusJob = (request.getHasOnCampusJob() != null && request.getHasOnCampusJob()) ? "Yes" : "No";
        String hasWorkStudy = (request.getHasWorkStudy() != null && request.getHasWorkStudy()) ? "Yes" : "No";
        String workState = (request.getWorkState() == null || request.getWorkState().trim().isEmpty() || 
                           "Same as residence".equalsIgnoreCase(request.getWorkState())) 
                           ? request.getResidentState() : request.getWorkState();

        PromptTemplate promptTemplate = PromptTemplate.from(TAX_GUIDANCE_PROMPT_TEMPLATE);
        Prompt prompt = promptTemplate.apply(Map.of(
                "hasOnCampusJob", hasOnCampusJob,
                "hasWorkStudy", hasWorkStudy,
                "residentState", request.getResidentState(),
                "workState", workState
        ));

        String response = chatLanguageModel.generate(prompt.text());

        // Parse response into structured sections
        List<TaxSection> sections = parseIntoSections(response);
        List<String> links = extractLinks(response);

        // Add standard TurboTax links if not already present
        addStandardLinks(links);

        String summary = generateSummary(request, sections);

        return new TaxAgentResponse(sections, links, summary);
    }

    private List<TaxSection> parseIntoSections(String response) {
        List<TaxSection> sections = new ArrayList<>();
        
        // Extract the steps section (everything before LINKS:)
        int linksIndex = response.toLowerCase().indexOf("links:");
        String stepsText = linksIndex > 0 ? response.substring(0, linksIndex) : response;
        
        // Remove "STEPS:" header if present
        stepsText = stepsText.replaceAll("(?i)^STEPS:\\s*", "").trim();
        
        String[] lines = stepsText.split("\n");
        TaxSection currentSection = null;
        TaxSubsection currentSubsection = null;
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }
            
            // Check if this is a section header (main heading with **)
            if (isSectionHeader(line)) {
                // Save previous section if exists
                if (currentSection != null) {
                    sections.add(currentSection);
                }
                // Start new section
                String sectionTitle = removeMarkdown(line);
                if (sectionTitle.endsWith(":")) {
                    sectionTitle = sectionTitle.substring(0, sectionTitle.length() - 1).trim();
                }
                currentSection = new TaxSection(sectionTitle);
                currentSubsection = null;
            }
            // Check if this is a subsection header (has **title:**)
            else if (isSubsectionHeader(line)) {
                if (currentSection == null) {
                    // Create a default section if we don't have one
                    currentSection = new TaxSection("Tax Guidance");
                }
                
                // Extract subsection title and potential content
                String cleanLine = removeMarkdown(line);
                String[] parts = cleanLine.split(":", 2);
                String subsectionTitle = parts[0].trim();
                String initialContent = parts.length > 1 ? parts[1].trim() : "";
                
                currentSubsection = new TaxSubsection(subsectionTitle, initialContent);
                currentSection.getSubsections().add(currentSubsection);
            }
            // Regular content line
            else if (!line.isEmpty()) {
                String cleanLine = removeMarkdown(line);
                if (!cleanLine.isEmpty()) {
                    if (currentSubsection != null) {
                        // Add to current subsection content
                        if (currentSubsection.getContent().isEmpty()) {
                            currentSubsection.setContent(cleanLine);
                        } else {
                            currentSubsection.setContent(currentSubsection.getContent() + " " + cleanLine);
                        }
                    } else if (currentSection != null) {
                        // Add as a regular item to the section
                        currentSection.getItems().add(cleanLine);
                    } else {
                        // Create a default section for orphaned items
                        currentSection = new TaxSection("Tax Guidance");
                        currentSection.getItems().add(cleanLine);
                    }
                }
            }
        }
        
        // Add the last section
        if (currentSection != null) {
            sections.add(currentSection);
        }
        
        // Fallback if no sections found
        if (sections.isEmpty()) {
            TaxSection defaultSection = new TaxSection("Tax Guidance");
            defaultSection.getItems().add("Please consult with a tax professional for personalized advice.");
            sections.add(defaultSection);
        }
        
        return sections;
    }
    
    private boolean isSectionHeader(String line) {
        // Section headers are typically bold text at the start of a line
        // Pattern: **Text:** where Text is a main heading (usually longer phrases)
        String trimmed = line.trim();
        if (trimmed.startsWith("**") && trimmed.endsWith("**")) {
            String content = trimmed.substring(2, trimmed.length() - 2).trim();
            // Section headers typically end with :
            if (content.endsWith(":")) {
                // Section headers are usually longer phrases (more than 3 words typically)
                // and don't contain nested colons (which would indicate a subsection)
                return content.split("\\s+").length >= 3;
            }
        }
        return false;
    }
    
    private boolean isSubsectionHeader(String line) {
        // Subsection headers have **Title:** format where Title is shorter
        String trimmed = line.trim();
        if (trimmed.startsWith("**") && trimmed.contains(":**")) {
            // Extract the title part
            int endBold = trimmed.indexOf("**", 2);
            if (endBold > 0) {
                String title = trimmed.substring(2, endBold).trim();
                // Subsection titles are typically shorter (1-4 words)
                int wordCount = title.split("\\s+").length;
                return wordCount <= 4;
            }
            return true;
        }
        return false;
    }
    
    private String removeMarkdown(String text) {
        // Remove bold markdown **text**
        text = text.replaceAll("\\*\\*([^*]+)\\*\\*", "$1");
        // Remove italic markdown *text* or _text_
        text = text.replaceAll("\\*([^*]+)\\*", "$1");
        text = text.replaceAll("_([^_]+)_", "$1");
        // Remove bullet markers
        text = text.replaceAll("^[-*•]\\s*", "");
        // Remove numbered list markers
        text = text.replaceAll("^\\d+[.)]\\s*", "");
        return text.trim();
    }

    private List<String> extractLinks(String response) {
        List<String> links = new ArrayList<>();
        
        // Extract links section
        String linksSection = extractSection(response, "LINKS:");
        
        // Pattern to match URLs
        Pattern urlPattern = Pattern.compile("https?://[^\\s]+");
        
        if (linksSection != null) {
            Matcher matcher = urlPattern.matcher(linksSection);
            while (matcher.find()) {
                String link = matcher.group();
                // Remove trailing punctuation
                link = link.replaceAll("[.,;:!?]+$", "");
                if (!links.contains(link)) {
                    links.add(link);
                }
            }
        }
        
        // Also search entire response for TurboTax URLs
        Matcher matcher = urlPattern.matcher(response);
        while (matcher.find()) {
            String link = matcher.group();
            link = link.replaceAll("[.,;:!?]+$", "");
            if (link.contains("turbotax.intuit.com") && !links.contains(link)) {
                links.add(link);
            }
        }

        return links;
    }

    private String extractSection(String text, String sectionHeader) {
        int startIndex = text.indexOf(sectionHeader);
        if (startIndex == -1) {
            // Try case-insensitive search
            String lowerText = text.toLowerCase();
            String lowerHeader = sectionHeader.toLowerCase();
            startIndex = lowerText.indexOf(lowerHeader);
            if (startIndex == -1) {
                return null;
            }
        }
        
        // Move past the header
        startIndex += sectionHeader.length();
        
        // Find the end of the section (either next section header or end of text)
        int endIndex = text.length();
        String[] nextSections = {"LINKS:", "SUMMARY:", "\n\n"};
        for (String nextSection : nextSections) {
            int nextIndex = text.indexOf(nextSection, startIndex);
            if (nextIndex != -1 && nextIndex < endIndex) {
                endIndex = nextIndex;
            }
        }
        
        return text.substring(startIndex, endIndex).trim();
    }

    private void addStandardLinks(List<String> links) {
        List<String> standardLinks = Arrays.asList(
                "https://turbotax.intuit.com/tax-tips/college-credits-deductions/student-tax-guide/L8K5vVfwJ",
                "https://turbotax.intuit.com/tax-tips/state-taxes/",
                "https://turbotax.intuit.com/tax-tips/college-credits-deductions/"
        );
        
        for (String standardLink : standardLinks) {
            if (!links.contains(standardLink)) {
                links.add(standardLink);
            }
        }
    }

    private String generateSummary(TaxAgentRequest request, List<TaxSection> sections) {
        StringBuilder summary = new StringBuilder();
        summary.append("Tax guidance for a student");
        
        if (request.getHasOnCampusJob() != null && request.getHasOnCampusJob()) {
            summary.append(" with an on-campus job");
        }
        if (request.getHasWorkStudy() != null && request.getHasWorkStudy()) {
            summary.append(" participating in work-study");
        }
        
        summary.append(" residing in ").append(request.getResidentState());
        
        if (request.getWorkState() != null && !request.getWorkState().trim().isEmpty() && 
            !"Same as residence".equalsIgnoreCase(request.getWorkState()) &&
            !request.getWorkState().equals(request.getResidentState())) {
            summary.append(" and working in ").append(request.getWorkState());
        }
        
        int totalItems = sections.stream()
                .mapToInt(s -> s.getSubsections().size() + s.getItems().size())
                .sum();
        summary.append(". ").append(sections.size()).append(" sections with ").append(totalItems).append(" key points identified.");
        
        return summary.toString();
    }
}

