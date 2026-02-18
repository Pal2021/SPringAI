package com.MrPal.ColdEmailAgents.controller;


import com.MrPal.ColdEmailAgents.model.AgentResponse;
import com.MrPal.ColdEmailAgents.service.ResumeEmailAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResumeAgentController {

    private final ResumeEmailAgent resumeEmailAgent;

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AgentResponse> processResume(
            @RequestPart("resume") MultipartFile resumeFile) {

        log.info("Received resume upload: name={}, size={} bytes",
                resumeFile.getOriginalFilename(), resumeFile.getSize());

        // Validate file
        if (resumeFile.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    AgentResponse.builder()
                            .status("ERROR")
                            .message("Uploaded file is empty.")
                            .build());
        }

        String contentType = resumeFile.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            return ResponseEntity.badRequest().body(
                    AgentResponse.builder()
                            .status("ERROR")
                            .message("Only PDF files are accepted. Received: " + contentType)
                            .build());
        }

        AgentResponse response = resumeEmailAgent.process(resumeFile);

        if ("ERROR".equals(response.getStatus())) {
            return ResponseEntity.internalServerError().body(response);
        }

        return ResponseEntity.ok(response);
    }


}
