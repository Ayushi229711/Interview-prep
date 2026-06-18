package com.ayushi.interview_prep.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate =
            new RestTemplate();

    private String callGemini(String prompt) {


        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                + apiKey;

        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);

        JsonArray parts = new JsonArray();
        parts.add(textPart);

        JsonObject content = new JsonObject();
        content.add("parts", parts);

        JsonArray contents = new JsonArray();
        contents.add(content);

        JsonObject requestBody = new JsonObject();
        requestBody.add("contents", contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request =
                new HttpEntity<>(
                        requestBody.toString(),
                        headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        url,
                        request,
                        String.class);

        JsonObject json =
                JsonParser.parseString(
                                response.getBody())
                        .getAsJsonObject();

        return json
                .getAsJsonArray("candidates")
                .get(0)
                .getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0)
                .getAsJsonObject()
                .get("text")
                .getAsString();
    }

    public String generateQuestions(
            String role,
            String difficulty) {

        String prompt =
                "Generate 5 "
                        + difficulty
                        + " interview questions for "
                        + role
                        + ". Return only questions.";

        return callGemini(prompt);
    }

    public String evaluateAnswer(
            String question,
            String answer) {

        String prompt =
                """
                Evaluate this interview answer.
        
                Question:
                %s
        
                Answer:
                %s
        
                Return ONLY in this format:
        
                Score: 8
        
                Feedback: Good explanation.
                """.formatted(question, answer);

        return callGemini(prompt);
    }
}