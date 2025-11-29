package com.studentapp.backend.service;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.data.message.AiMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillExtractionService {

    private final ChatLanguageModel chatLanguageModel;

    public String extractBillDetails(byte[] imageData, String mimeType, List<String> availableCategories) {
        String categoriesStr = String.join(", ", availableCategories);
        String promptText = String.format(
                "Analyze this bill image and extract the following details:\n" +
                        "1. Date (in YYYY-MM-DD format)\n" +
                        "2. Total Amount (number only)\n" +
                        "3. Category (choose the best match from this list: [%s])\n\n" +
                        "Return ONLY a JSON object with keys: 'date', 'amount', 'category'. " +
                        "Do not include markdown formatting like ```json ... ```.",
                categoriesStr
        );

        String base64Image = Base64.getEncoder().encodeToString(imageData);

        UserMessage userMessage = UserMessage.from(
                TextContent.from(promptText),
                ImageContent.from(base64Image, mimeType)
        );

        Response<AiMessage> response = chatLanguageModel.generate(userMessage);
        String content = response.content().text();
        
        // Clean up markdown code blocks if present
        content = content.replaceAll("```json", "").replaceAll("```", "").trim();
        
        return content;
    }
}
