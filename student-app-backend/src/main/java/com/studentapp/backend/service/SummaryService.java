package com.studentapp.backend.service;

import com.studentapp.backend.dto.SummaryRequest;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final ChatLanguageModel chatLanguageModel;
    private final com.studentapp.backend.repository.SummaryHistoryRepository summaryHistoryRepository;

    private static final String SUMMARY_PROMPT_TEMPLATE = 
            "You are a helpful assistant. Please explain the following topic or text in exactly one paragraph, " +
            "in the easiest way possible (like explaining to a beginner). " +
            "Ensure the content is safe and appropriate. If the content is inappropriate, return 'CONTENT_VIOLATION'.\n\n" +
            "Topic/Text:\n{{text}}";

    private static final String VIDEO_SUMMARY_PROMPT_TEMPLATE = 
            "You are an AI that generates detailed, structured, and accurate lecture notes from transcriptions. " +
            "The format must be markdown. " +
            "Strictly adhere to the following guardrails:\n" +
            "1. If the content is not educational or related to the video topic, return 'CONTENT_VIOLATION'.\n" +
            "2. Do not answer questions unrelated to the provided transcript.\n\n" +
            "Generate detailed and structured lecture notes from the following transcription:\n{{text}}\n\n" +
            "Guidelines:\n" +
            "- Organize into clear sections (Introduction, Key Concepts, Examples, Summary).\n" +
            "- Include definitions, explanations, and key points.\n" +
            "- Use bullet points.\n" +
            "- Maintain accuracy.";

    public String generateSummary(SummaryRequest request, com.studentapp.common.model.User user) {
        // Basic Guardrail
        if (request.getText() == null || request.getText().trim().length() < 10) {
            throw new IllegalArgumentException("Text is too short or empty.");
        }

        String summary;
        if (request.getSourceType() == SummaryRequest.SourceType.YOUTUBE) {
            summary = generateVideoSummary(request.getText());
        } else {
            // Default Text Summary
            PromptTemplate promptTemplate = PromptTemplate.from(SUMMARY_PROMPT_TEMPLATE);
            Prompt prompt = promptTemplate.apply(Map.of("text", request.getText()));
            String response = chatLanguageModel.generate(prompt.text());
            summary = validateResponse(response);
        }

        // Save History
        com.studentapp.backend.model.SummaryHistory history = com.studentapp.backend.model.SummaryHistory.builder()
                .user(user)
                .requestContent(request.getText())
                .responseSummary(summary)
                .requestType(request.getSourceType() != null ? request.getSourceType().name() : "TEXT")
                .build();
        summaryHistoryRepository.save(history);

        return summary;
    }

    public java.util.List<com.studentapp.backend.model.SummaryHistory> getUserHistory(com.studentapp.common.model.User user) {
        return summaryHistoryRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    private String generateVideoSummary(String text) {
        // Chunking Logic (Threshold: 50,000 chars)
        if (text.length() > 50000) {
            return generateChunkedSummary(text);
        }

        PromptTemplate promptTemplate = PromptTemplate.from(VIDEO_SUMMARY_PROMPT_TEMPLATE);
        Prompt prompt = promptTemplate.apply(Map.of("text", text));
        String response = chatLanguageModel.generate(prompt.text());
        return validateResponse(response);
    }

    private String generateChunkedSummary(String text) {
        int chunkSize = 40000;
        int overlap = 2000;
        StringBuilder finalSummary = new StringBuilder();
        
        for (int i = 0; i < text.length(); i += (chunkSize - overlap)) {
            int end = Math.min(text.length(), i + chunkSize);
            String chunk = text.substring(i, end);
            
            PromptTemplate promptTemplate = PromptTemplate.from(VIDEO_SUMMARY_PROMPT_TEMPLATE);
            Prompt prompt = promptTemplate.apply(Map.of("text", chunk));
            String response = chatLanguageModel.generate(prompt.text());
            
            if (!"CONTENT_VIOLATION".equals(response)) {
                finalSummary.append(response).append("\n\n");
            }
        }
        
        return finalSummary.toString();
    }

    private String validateResponse(String response) {
        if ("CONTENT_VIOLATION".equals(response)) {
            throw new IllegalArgumentException("The content contains inappropriate or irrelevant material.");
        }
        return response;
    }
}
