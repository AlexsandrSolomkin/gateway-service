package com.example.gatewayservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/users")
    public ResponseEntity<String> userFallback() {
        return ResponseEntity.ok("Сервис пользователей временно недоступен. Попробуйте позже.");
    }
}
