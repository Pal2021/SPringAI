package com.spring_ai_tools.spring_ai_tools_calling.Tools;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;

    // Spring AI automatically injects the correct starter's Builder here
    public ChatService(ChatClient.Builder chatClientBuilder, TimeTools timeTools) {
        this.chatClient = chatClientBuilder
                .defaultTools(timeTools) // Attaches our tool seamlessly across any LLM vendor
                .build();
    }

    public String ask(String userMessage) {
        return this.chatClient.prompt()
                .user(userMessage) // userMessage= "What is the current time in new york?"
                .call()
                .content();
    }
}