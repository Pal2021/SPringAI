package com.Mrpal.demo;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/document")
public class DocumentQaController {
    private final DocumentQaService documentQaService;

    public DocumentQaController(DocumentQaService documentQaService) {
        this.documentQaService = documentQaService;
    }
    @PostMapping("/qa")
    public String qa(@RequestBody Map<String,String> req) {

        return documentQaService.qaOverDocument(
                req.get("document")
        );
    }
}
