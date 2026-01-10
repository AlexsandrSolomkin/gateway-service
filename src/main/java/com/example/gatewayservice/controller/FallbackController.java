package com.example.gatewayservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/users")
    public Mono<ResponseEntity<String>> userFallback() {
        // Возвращаем ответ через Mono для WebFlux
        return Mono.just(ResponseEntity.ok("Сервис пользователей временно недоступен. Попробуйте позже."));
    }
}
