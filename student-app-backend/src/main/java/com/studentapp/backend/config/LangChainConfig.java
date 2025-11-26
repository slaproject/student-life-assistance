package com.studentapp.backend.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LangChainConfig {
    
    @Value("${langchain4j.google-ai-gemini.chat-model.api-key}")
    private String apiKey;
    
    @Value("${langchain4j.google-ai-gemini.chat-model.model-name:models/gemini-1.5-flash}")
    private String modelName;
    
    @Value("${langchain4j.google-ai-gemini.chat-model.temperature:0.7}")
    private Double temperature;
    
    @Value("${langchain4j.google-ai-gemini.chat-model.timeout:60s}")
    private String timeout;
    
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .timeout(Duration.parse("PT" + timeout.replace("s", "S")))
                .build();
    }
}
