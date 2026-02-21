package com.Mrpal.demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        // We define the blueprint here; we DO NOT execute LLM calls here.
        return chatClientBuilder
                .defaultOptions(GoogleGenAiChatOptions.builder()
                        .temperature(0.4)
                        .build())
                .defaultSystem("""
                        You are an internal HR assistant. Your role is to help 
                        employees with questions related to HR policies, such as 
                        leave policies, working hours, benefits, and code of conduct.
                        If a user asks for help with anything outside of these topics, 
                        kindly inform them that you can only assist with queries related to 
                        HR policies.
                        """)
                .build();
    }


}