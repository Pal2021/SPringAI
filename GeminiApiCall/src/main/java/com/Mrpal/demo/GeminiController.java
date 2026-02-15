package com.Mrpal.demo;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/generate")
    public String generate(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        return geminiService.getCompletion(prompt);
    }
}