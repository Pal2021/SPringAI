package com.Mrpal.demo.service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class HrAssistantService {

    private final ChatClient chatClient;

    public HrAssistantService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String askQuestion(String userQuery) {
        // THIS is where the execution should happen at runtime
        return chatClient.prompt()
                .user(userQuery)
                .call()
                .content();

    }

    public String testPirateQuery() {
        // Testing our HR constraints to see if the AI successfully rejects the prompt
        return chatClient.prompt()
                .user("Generate the names of 5 famous pirates.")
                .call()
                .content();
    }
    // Inside HrAssistantService.java
    public Flux<String> streamQuestion(String userQuery) {
        return chatClient.prompt()
                .user(userQuery)
                .stream()
                .content();
    }
}