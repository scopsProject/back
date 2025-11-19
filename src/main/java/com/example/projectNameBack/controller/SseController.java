package com.example.projectNameBack.controller;

import com.example.projectNameBack.service.SseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseController {

    private final SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping("/sse/subscribe")
    public SseEmitter subscribe() {
        // 🔥 직접 처리하지 않고 서비스 함수를 호출합니다.
        return sseService.subscribe();
    }
}