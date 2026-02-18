package com.Mrpal.demo.controller;

import com.Mrpal.demo.dto.EmailAgentRequest;
import com.Mrpal.demo.service.EmailAgentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent")
public class EmailAgentController {

    private final EmailAgentService emailAgentService;

    public EmailAgentController(EmailAgentService emailAgentService) {
        this.emailAgentService = emailAgentService;
    }
    @PostMapping("/generate")
    public String generate(@RequestBody EmailAgentRequest req) {
        return emailAgentService.generateEmail(req);
    }
}