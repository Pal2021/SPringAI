package com.Mrpal.demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.MysqlChatMemoryRepositoryDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ChatMemoryConfig {

    @Bean
    public ChatMemory chatMemory(JdbcTemplate jdbcTemplate, PlatformTransactionManager transactionManager) {

        // 🔹 STEP 1: Create a repository (DB layer for storing chat history)
        // This handles all SQL operations like save, fetch, delete
        ChatMemoryRepository repository = JdbcChatMemoryRepository.builder()

                // Spring JDBC helper to run SQL queries
                .jdbcTemplate(jdbcTemplate)

                // Ensures DB operations are safe (commit/rollback)
                .transactionManager(transactionManager)

                // Specifies SQL dialect for MySQL (important for compatibility)
                .dialect(new MysqlChatMemoryRepositoryDialect())

                // Build the repository
                .build();


        // 🔹 STEP 2: Wrap repository with memory strategy (how much history to use)
        return MessageWindowChatMemory.builder()

                // Connects DB storage to memory system
                .chatMemoryRepository(repository)

                // Only last 10 messages are sent to LLM (sliding window)
                // Older messages remain in DB but are NOT sent to model
                .maxMessages(10)

                // Build memory object
                .build();
    }

    @Bean("chatMemoryChatClient")
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {

        return builder

                // 🔹 Attach advisor (this is the "automation layer")
                // It automatically:
                // 1. Fetches past messages from DB before API call
                // 2. Adds them to prompt
                // 3. Saves new messages after response
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )

                // Build ChatClient (used in controller)
                .build();
    }
}