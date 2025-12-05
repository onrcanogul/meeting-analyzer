package com.example.demo.starter.application.service.ai.impl;

import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.service.ai.OpenAIService;
import com.example.demo.starter.application.service.auth.CustomUserDetailsService;
import com.example.demo.starter.domain.entity.Meeting;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAIServiceImpl implements OpenAIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CustomUserDetailsService userService;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.url}")
    private String openAiApiUrl;

    @Override
    public ServiceResponse<List<ProductBacklogItemDto>> analyzeBacklog(Meeting meeting) {
        try {
            String prompt = buildPrompt(meeting);

            Map<String, Object> requestBody = getBody(prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(openAiApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(openAiApiUrl, entity, Map.class);
            String content = extractContent(response.getBody());

            List<ProductBacklogItemDto> items = parseItems(content);

            return ServiceResponse.success(items, 200);
        } catch (Exception ex) {
        log.error("Error while calling OpenAI API", ex);
        return ServiceResponse.failure("Error while prompting: " + ex.getMessage(), 500);
        }
    }

    private String buildPrompt(Meeting meeting) {
        try {
            Map<String, Object> inner = new HashMap<>();
            inner.put("title", meeting.getTitle());
            inner.put("status", meeting.getStatus() != null ? meeting.getStatus().name() : null);
            inner.put("transcript", meeting.getTranscript());

            Map<String, Object> outer = new HashMap<>();
            outer.put("meeting", inner);

            String meetingJson = objectMapper.writeValueAsString(outer);


            return """
            You are an AI assistant that analyzes software development meetings
            and extracts as many actionable Product Backlog Items as possible.
            
            Each backlog item must represent a distinct technical or functional action and much detail explanation for each step,
            such as creating a new component, modifying logic, testing, refactoring, or deployment planning.
            
            The following meeting information is provided:
            %s
            
            Decompose every discussed decision or task into SEPARATE backlog items.
            Be exhaustive — even small tasks, testing steps, or sub-decisions should become individual PBIs.
            
            Return the result as a valid JSON array of objects.
            Each object must have:
            - title
            - description
            - priority (HIGH, MEDIUM, or LOW)
            - acceptanceCriteria (string, can include multiple bullet points)
            
            Rules:
            - If a task can be logically divided into smaller parts (e.g. backend, frontend, deployment), split them.
            - Include any follow-up or review actions as separate backlog items.
            - Avoid merging multiple actions into one PBI.
            - Do not include any explanation, markdown, or comments — only valid JSON.
            
            """.formatted(meetingJson);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize meeting object", e);
        }
    }


    private Map<String, Object> getBody(String prompt) {
        return Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are an assistant that extracts backlog items in JSON format."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.3
        );
    }

    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> body) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
        return (String) ((Map<String, Object>) choices.getFirst().get("message")).get("content");
    }

    private List<ProductBacklogItemDto> parseItems(String json) throws Exception {
        String cleaned = json
                .replaceAll("(?s)```json", "")
                .replaceAll("```", "")
                .trim();
        return objectMapper.readValue(cleaned, new TypeReference<>() {});
    }
}

