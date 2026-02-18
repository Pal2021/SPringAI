package com.MrPal.ColdEmailAgents.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PdfReaderService {

    /**
     * Extracts raw text from an uploaded PDF resume using Spring AI's PDF reader.
     */
    public String extractTextFromPdf(MultipartFile file) throws IOException {
        log.info("Extracting text from PDF: {}", file.getOriginalFilename());

        // Wrap MultipartFile bytes into a Spring Resource
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        // Configure PDF reader
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPagesPerDocument(1)   // one Document object per page
                .build();

        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource, config);
        List<Document> pages = pdfReader.get();

        // Concatenate all pages into a single text block
        String fullText = pages.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        log.debug("Extracted {} characters from PDF", fullText.length());
        return fullText;
    }
}