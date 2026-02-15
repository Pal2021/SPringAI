package com.Mrpal.demo;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public String getCompletion(String prompt) {
        // Create client with explicit API key
        Client client = Client.builder()
                .apiKey(apiKey)
                .build();

        GenerateContentResponse response = client.models.generateContent(

                "gemini-2.5-flash",
                prompt,
                null
        );

        return response.text();
    }
}