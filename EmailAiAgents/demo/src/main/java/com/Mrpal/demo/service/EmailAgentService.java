package com.Mrpal.demo.service;

import com.Mrpal.demo.dto.EmailAgentRequest;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class EmailAgentService {

    private final Client client;

    public EmailAgentService(Client client) {
        this.client = client;
    }

    public String generateEmail(EmailAgentRequest req) {

        // Basic validation (VERY important)
        if (req.getName() == null || req.getCompany() == null ||
                req.getRole() == null || req.getProblem() == null) {
            return "Missing required fields";
        }

        String systemPrompt = """
You are a B2B sales assistant.
Write short, professional, personalized outreach emails.
Use ONLY the provided information.
Do NOT invent company facts.
""";

        String prompt = """
SYSTEM:
%s

CUSTOMER_NAME: %s
COMPANY: %s
ROLE: %s
PROBLEM: %s

TASK:
Write a personalized cold email (max 120 words).
Include:
- Greeting
- Problem acknowledgment
- Value proposition
- Soft call-to-action

Return ONLY the email body.
""".formatted(
                systemPrompt,
                req.getName(),
                req.getCompany(),
                req.getRole(),
                req.getProblem()
        );

        GenerateContentResponse response =
                client.models.generateContent("gemini-2.5-flash", prompt, null);

        return response.text();
    }
}
