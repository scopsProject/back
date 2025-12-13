package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.EventRequestDto;
import com.example.projectNameBack.entity.Event;
import com.example.projectNameBack.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 🔥 이거 import 필수!
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j // 🔥 [핵심] 이 어노테이션을 붙이면 'log' 변수를 바로 쓸 수 있습니다.
@RestController
@RequestMapping("/songs/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/new")
    public ResponseEntity<?> createEvent(@RequestBody EventRequestDto dto) {
        // System.out.println 대신 log.info 사용
        log.info("=== [EventController] 새 행사 등록 요청 ===");
        log.info("요청 행사명: {}", dto.getEventName()); // {} 자리에 변수가 쏙 들어갑니다.

        try {
            Event newEvent = eventService.createEvent(dto);
            return ResponseEntity.ok(newEvent);
        } catch (IllegalArgumentException e) {
            log.warn("등록 실패(잘못된 요청): {}", e.getMessage()); // warn: 경고 수준
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("행사 등록 중 서버 에러 발생", e); // error: 심각한 에러 (스택트레이스 포함)
            return ResponseEntity.internalServerError().body("행사 등록 실패");
        }
    }

    @GetMapping("/period")
    public ResponseEntity<List<Event>> getEventsByPeriod(
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        log.info("=== [EventController] 기간별 행사 조회 요청 진입 ===");
        log.info("조회 기간: {} ~ {}", start, end);

        List<Event> events = eventService.getEventsByPeriod(start, end);

        log.info("컨트롤러 응답 예정 데이터 개수: {}개", events.size());
        return ResponseEntity.ok(events);
    }
    @GetMapping("/available")
    public ResponseEntity<List<String>> getAvailableEvents() {
        return ResponseEntity.ok(eventService.getAvailableEventNames());
    }
}