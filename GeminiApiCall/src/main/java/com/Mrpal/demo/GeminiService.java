package com.Mrpal.demo;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;

    public GeminiService(Client client) {
        this.client = client;
    }

    public String basicChat(String prompt) {

        String systemPrompt =
                "You are a doctor. Answer clearly and concisely.";

        String finalPrompt = """
                SYSTEM:
                %s

                USER:
                %s
                """.formatted(systemPrompt, prompt);

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        finalPrompt,
                        null
                );

        return response.text();
    }
}
