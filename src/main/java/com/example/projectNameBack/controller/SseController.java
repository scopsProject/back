package com.example.projectNameBack.controller;

import com.example.projectNameBack.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@RequiredArgsConstructor
@RestController
public class SseController {

    private final SseService sseService;

    @GetMapping("/sse/subscribe")
    public SseEmitter subscribe() {
        return sseService.subscribe();
    }
}