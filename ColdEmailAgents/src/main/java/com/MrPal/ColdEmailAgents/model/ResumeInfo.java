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
public class ResumeInfo {
    private String candidateName;
    private String currentRole;
    private String yearsOfExperience;
    private List<String> topSkills;
    private String email;
    private String phone;
    private String currentCompany;
    private String summary;

}
