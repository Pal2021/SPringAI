package com.spring_ai_tools.spring_ai_tools_calling.Tools;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

    private final ChatService chatService;

    public AppRunner(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Spring AI Tool Calling Demo ===\n");

        // Single question test to verify the new key works safely
        String testQuestion = "What is the current time in newyork?";

        System.out.println("User : " + testQuestion);
        try {
            System.out.println("AI   : " + chatService.ask(testQuestion));
        } catch (Exception e) {
            System.out.println("Execution failed: " + e.getMessage());
        }
    }
}