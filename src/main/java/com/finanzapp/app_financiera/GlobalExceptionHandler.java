package com.finanzapp.app_financiera;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
            Map<String, Object> body = new HashMap<>();
            body.put("code", ex.getStatusCode().value());
            body.put("message", ex.getReason());
            return new ResponseEntity<>(body, ex.getStatusCode());
        }
}
