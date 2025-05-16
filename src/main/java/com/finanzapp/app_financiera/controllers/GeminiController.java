package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.dtos.ResponseMessage;
import com.finanzapp.app_financiera.services.GeminiService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;
    
    @Autowired
    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/content/{userId}")
    public ResponseEntity<String> generateContent(@PathVariable @Parameter(description = "ID del usuario para obtener los totales") int userId, @RequestBody ResponseMessage request) {
        String result = geminiService.handleGeminiRequest(userId, request.getMessage());
        return ResponseEntity.ok(result);
    }
}
