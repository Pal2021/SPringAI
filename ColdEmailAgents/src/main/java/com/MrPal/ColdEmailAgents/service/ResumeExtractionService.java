package com.MrPal.ColdEmailAgents.service;

import com.MrPal.ColdEmailAgents.model.ResumeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeExtractionService {

    private final Client chatClient;
    private final ObjectMapper objectMapper;

    private static final String EXTRACTION_SYSTEM_PROMPT = """
            You are a precise resume parser. Extract information from the resume text provided.
            Always respond with ONLY a valid JSON object — no markdown, no extra text.
            
            JSON format:
            {
              "candidateName": "Full Name",
              "currentRole": "Most recent job title",
              "currentCompany": "Most recent employer",
              "yearsOfExperience": "e.g. 1, 2-3, 5+",
              "topSkills": ["Skill1", "Skill2"],
              "email": "email@example.com",
              "phone": "+91-XXXXXXXXXX",
              "summary": "One sentence summary of the candidate profile"
            }
            
            Rules:
            - topSkills: extract exactly 2 most prominent technical skills
            - yearsOfExperience: calculate from work history dates; return "Fresher" if none
            - If a field is not found, return null
            """;

    /**
     * Uses Spring AI ChatClient to extract structured resume info from raw text.
     */
    public ResumeInfo extractResumeInfo(String resumeText) {
        log.info("Extracting structured info from resume text...");

        String userMessage = "Parse this resume and return JSON:\n\n" + resumeText;
        String fullPrompt = EXTRACTION_SYSTEM_PROMPT + "\n\n" + userMessage;

        String jsonResponse = chatClient.models.generateContent(
                "gemini-2.5-flash",   // or gemini-2.5-flash
                fullPrompt,
                null
        ).text();

        log.debug("Raw AI extraction response: {}", jsonResponse);

        try {
            String cleaned = jsonResponse
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();
            return objectMapper.readValue(cleaned, ResumeInfo.class);
        } catch (Exception e) {
            log.error("Failed to parse extraction JSON: {}", e.getMessage());
            return ResumeInfo.builder()
                    .candidateName("Unknown")
                    .currentRole("Unknown")
                    .topSkills(List.of("Java", "Spring Boot"))
                    .summary(resumeText.substring(0, Math.min(200, resumeText.length())))
                    .build();
        }
    }
}