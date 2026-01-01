package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.ReservationRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        // 1. 타임아웃 설정 (1시간)
        Long timeout = 60L * 60 * 1000;
        SseEmitter emitter = new SseEmitter(timeout);
        emitters.add(emitter);

        // 2. 비동기 요청이 완료되었을 때 (정상 종료)
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
        });

        // 3. 타임아웃 발생 시
        emitter.onTimeout(() -> {
            emitter.complete(); // 브라우저에게 연결 종료를 알림 (예외 발생 방지)
            emitters.remove(emitter);
        });

        // 4. 에러 발생 시
        emitter.onError((e) -> {
            emitter.complete(); // 에러 발생 시에도 안전하게 종료
            emitters.remove(emitter);
        });

        // 5. 초기 연결 성공 메시지 전송 (503 에러 방지용 더미 데이터)
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected!"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }
        return emitter;
    }

    public void broadcast(ReservationRequestDto dto) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("new-reservation").data(dto));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}