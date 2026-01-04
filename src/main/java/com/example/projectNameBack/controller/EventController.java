package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.EventDto;
import com.example.projectNameBack.dto.EventRequestDto;
import com.example.projectNameBack.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 1. 새 행사 등록 (Event -> EventDto)
    @PostMapping("/new")
    public ResponseEntity<EventDto> createEvent(@RequestBody EventRequestDto dto) {
        return ResponseEntity.ok(eventService.createEvent(dto));
    }

    // 2. 모든 행사 이름 조회 (String List는 그대로)
    @GetMapping("/names/all")
    public ResponseEntity<List<String>> getAllEventNames() {
        return ResponseEntity.ok(eventService.getAllEventNames());
    }

    // 3. 기간별 행사 조회 (List<Event> -> List<EventDto>)
    @GetMapping("/period")
    public ResponseEntity<List<EventDto>> getEventsByPeriod(
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        return ResponseEntity.ok(eventService.getEventsByPeriod(start, end));
    }

    // 4. 곡 등록 가능한 행사 목록 조회
    @GetMapping("/available")
    public ResponseEntity<List<String>> getAvailableEvents() {
        return ResponseEntity.ok(eventService.getAvailableEventNames());
    }
}