package com.MrPal.ColdEmailAgents.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse {

    private ResumeInfo extractedInfo;
    private List<EmailVariant> emailVariants;
    private String status;
    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailVariant {
        private String label;       // e.g. "Direct & Confident", "Warm & Friendly", "Achievement-Focused"
        private String subject;
        private String body;
    }
}