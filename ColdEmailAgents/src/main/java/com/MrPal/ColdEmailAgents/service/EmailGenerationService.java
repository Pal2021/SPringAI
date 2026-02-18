package com.MrPal.ColdEmailAgents.service;
import com.MrPal.ColdEmailAgents.model.AgentResponse;
import com.MrPal.ColdEmailAgents.model.ResumeInfo;
import com.google.genai.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailGenerationService {

    private final Client chatClient;

    private static final String EMAIL_SYSTEM_PROMPT = """
            You are an expert career coach writing cold outreach emails to recruiters.
            Generate exactly 3 cold email variants, each under 120 words.
            
            Rules for all emails:
            - Mention Top  7 technical skills (provided)
            - No emojis, no generic AI phrases like "I hope this finds you well"
            - Human, direct tone
            - End with a clear call to action (e.g., "Could we schedule a 15-minute call this week?")
            - Include a compelling subject line for each
            
            Return ONLY a JSON array — no markdown, no extra text:
            [
              {
                "label": "Direct & Confident",
                "subject": "...",
                "body": "..."
              },
              {
                "label": "Warm & Personable",
                "subject": "...",
                "body": "..."
              },
              {
                "label": "Achievement-Focused",
                "subject": "...",
                "body": "..."
              }
            ]
            """;

    /**
     * Generates 3 cold email variants tailored to the extracted resume info.
     */
    public List<AgentResponse.EmailVariant> generateEmailVariants(ResumeInfo info) {
        log.info("Generating cold email variants for: {}", info.getCandidateName());

        String skill1 = info.getTopSkills() != null && !info.getTopSkills().isEmpty()
                ? info.getTopSkills().get(0) : "Spring Boot";
        String skill2 = info.getTopSkills() != null && info.getTopSkills().size() > 1
                ? info.getTopSkills().get(1) : "Microservices";

        String userMessage = String.format("""
            Generate 3 cold recruiter emails for this candidate:
            Name: %s
            Current Role: %s
            Current Company: %s
            Years of Experience: %s
            Top 7 Skills: %s, %s
            Email: %s
            Phone: %s
            Summary: %s
            """,
                info.getCandidateName(), info.getCurrentRole(), info.getCurrentCompany(),
                info.getYearsOfExperience(), skill1, skill2,
                info.getEmail(), info.getPhone(), info.getSummary());

        // FIX: combine prompts + call .text()
        String fullPrompt = EMAIL_SYSTEM_PROMPT + "\n\n" + userMessage;
        String jsonResponse = chatClient.models.generateContent("gemini-2.5-flash", fullPrompt, null).text();

        log.debug("Raw AI email response: {}", jsonResponse);

        try {
            String cleaned = jsonResponse
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();

            // Extract just the JSON array in case of extra text
            int start = cleaned.indexOf('[');
            int end = cleaned.lastIndexOf(']');
            if (start != -1 && end != -1) {
                cleaned = cleaned.substring(start, end + 1);
            }

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            AgentResponse.EmailVariant[] variants = mapper.readValue(cleaned, AgentResponse.EmailVariant[].class);
            return List.of(variants);
        } catch (Exception e) {
            log.error("Failed to parse email variants JSON: {}", e.getMessage());
            return List.of(AgentResponse.EmailVariant.builder()
                    .label("Default")
                    .subject("Software Engineer Open to Opportunities")
                    .body("Hi [Recruiter],\n\nI'm " + info.getCandidateName() +
                            ", a " + info.getCurrentRole() + " with expertise in " +
                            skill1 + " and " + skill2 + ". I'd love to connect.\n\n" +
                            "Could we schedule a quick call this week?\n\nBest,\n" +
                            info.getCandidateName())
                    .build());
        }
    }
}