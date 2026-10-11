package com.aichat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class OpenAiService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public OpenAiService(ObjectMapper objectMapper,
                         @Value("${openai.api-key}") String apiKey,
                         @Value("${openai.model}") String model) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .build();
    }

    public String ask(String message) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is not configured");
        }

        Map<String, Object> body = Map.of(
                "model", model,
                "input", message
        );

        String json = restClient.post()
                .uri("/responses")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(json);

            JsonNode outputText = root.get("output_text");
            if (outputText != null && !outputText.isNull()) {
                return outputText.asText();
            }

            JsonNode output = root.get("output");
            if (output != null && output.isArray()) {
                StringBuilder result = new StringBuilder();
                for (JsonNode item : output) {
                    JsonNode content = item.get("content");
                    if (content != null && content.isArray()) {
                        for (JsonNode part : content) {
                            JsonNode text = part.get("text");
                            if (text != null) {
                                result.append(text.asText());
                            }
                        }
                    }
                }
                if (!result.isEmpty()) return result.toString();
            }

            return "The AI returned an empty response.";
        } catch (Exception e) {
            throw new IllegalStateException("Could not read AI response", e);
        }
    }
}
