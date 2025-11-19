package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.ReservationRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    // 스레드 안전한 리스트 사용
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // 1. 구독(연결) 메서드
    public SseEmitter subscribe() {
        // 타임아웃 1시간
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        emitters.add(emitter);

        // 콜백 설정
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        // 503 에러 방지용 더미 데이터 전송
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected!"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    // 2. 알림 전송 메서드
    public void broadcast(ReservationRequestDto dto) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("new-reservation")
                        .data(dto));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}