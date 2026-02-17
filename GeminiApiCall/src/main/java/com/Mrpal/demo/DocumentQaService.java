package com.Mrpal.demo;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class DocumentQaService {

    public final Client client;

    public DocumentQaService(Client client) {
        this.client = client;
    }

   public String qaOverDocument(String questions){
       if (questions == null || questions.isBlank()) {
           return "QUESTION IS EMPTY";
       }
       String systemPrompt =
               "You are a engineer . Answer ONLY Engineer question . " +
                       "If the answers out of context , reply NOT FOUND.";
       String finalPrompt = """
                SYSTEM:
                %s

                QUESTION:
                %s
                """.formatted(systemPrompt,questions);
       GenerateContentResponse response =
               client.models.generateContent(
                       "gemini-2.5-flash",
                       finalPrompt,
                       null
               );

       return response.text();
   }
}
