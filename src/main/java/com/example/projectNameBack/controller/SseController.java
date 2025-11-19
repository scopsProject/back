package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.ReservationRequestDto;
import com.example.projectNameBack.service.SseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class SseController {
    private final SseService sseService;

    // 생성자 주입
    public SseController(SseService sseService) {
        this.sseService = sseService;
    }
    // 연결된 모든 클라이언트(브라우저)의 명단을 저장할 리스트 (Thread-Safe)
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /**
     * 클라이언트가 이 주소로 요청을 보내면 SSE 연결이 맺어짐
     * Endpoint: /api/sse/subscribe (경로는 편하신대로 설정)
     */
    @GetMapping("/sse/subscribe")
    public SseEmitter subscribe() {
        // 타임아웃 1시간 설정 (기본값은 보통 30초라 너무 짧음)
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        emitters.add(emitter);

        // 연결이 끊기거나 에러 발생 시 리스트에서 제거
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        // 최초 연결 시 더미 데이터 전송 (연결 즉시 503 에러 방지 및 연결 확인용)
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected!"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }

        return emitter;
    }
}