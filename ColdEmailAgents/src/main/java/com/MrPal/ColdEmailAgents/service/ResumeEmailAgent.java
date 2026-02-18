package com.MrPal.ColdEmailAgents.service;
import com.MrPal.ColdEmailAgents.model.AgentResponse;
import com.MrPal.ColdEmailAgents.model.ResumeInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ResumeEmailAgent — the central orchestrator.
 *
 * Pipeline:
 *   PDF Upload → Extract Text → Parse Resume Info → Generate Email Variants → Return Response
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeEmailAgent {

    private final PdfReaderService pdfReaderService;
    private final ResumeExtractionService extractionService;
    private final EmailGenerationService emailGenerationService;

    public AgentResponse process(MultipartFile resumePdf) {
        log.info("=== ResumeEmailAgent: Starting pipeline ===");

        try {
            // Step 1: Extract raw text from PDF
            log.info("Step 1/3 - Reading PDF...");
            String resumeText = pdfReaderService.extractTextFromPdf(resumePdf);

            if (resumeText == null || resumeText.isBlank()) {
                return AgentResponse.builder()
                        .status("ERROR")
                        .message("Could not extract text from the uploaded PDF. Please ensure it is not scanned/image-based.")
                        .build();
            }

            // Step 2: Extract structured candidate info via AI
            log.info("Step 2/3 - Extracting resume info via AI...");
            ResumeInfo resumeInfo = extractionService.extractResumeInfo(resumeText);

            // Step 3: Generate 3 cold email variants via AI
            log.info("Step 3/3 - Generating cold email variants via AI...");
            List<AgentResponse.EmailVariant> emailVariants = emailGenerationService.generateEmailVariants(resumeInfo);

            log.info("=== ResumeEmailAgent: Pipeline complete ===");

            return AgentResponse.builder()
                    .extractedInfo(resumeInfo)
                    .emailVariants(emailVariants)
                    .status("SUCCESS")
                    .message("Resume processed and " + emailVariants.size() + " email variants generated.")
                    .build();

        } catch (Exception e) {
            log.error("ResumeEmailAgent pipeline failed: {}", e.getMessage(), e);
            return AgentResponse.builder()
                    .status("ERROR")
                    .message("Agent failed: " + e.getMessage())
                    .build();
        }
    }
}