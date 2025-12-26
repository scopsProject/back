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

@Slf4j
@RestController
@RequestMapping("/songs/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 1. 새 행사 등록
    @PostMapping("/new")
    public ResponseEntity<Event> createEvent(@RequestBody EventRequestDto dto) {
        log.info("=== [EventController] 새 행사 등록 요청: {} ===", dto.getEventName());

        return ResponseEntity.ok(eventService.createEvent(dto));
    }
    @GetMapping("/names/all")
    public ResponseEntity<List<String>> getAllEventNames() {
        return ResponseEntity.ok(eventService.getAllEventNames());
    }

    // 2. 기간별 행사 조회
    @GetMapping("/period")
    public ResponseEntity<List<Event>> getEventsByPeriod(
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        log.info("=== [EventController] 기간별 행사 조회 요청 ({} ~ {}) ===", start, end);

        return ResponseEntity.ok(eventService.getEventsByPeriod(start, end));
    }

    // 3. 현재 신청 가능한 행사 목록
    @GetMapping("/available")
    public ResponseEntity<List<String>> getAvailableEvents() {
        return ResponseEntity.ok(eventService.getAvailableEventNames());
    }
}