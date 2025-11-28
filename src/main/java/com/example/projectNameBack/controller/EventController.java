package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.EventRequestDto;
import com.example.projectNameBack.entity.Event;
import com.example.projectNameBack.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/songs/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/new")
    public ResponseEntity<?> createEvent(@RequestBody EventRequestDto dto) {
        try {
            Event newEvent = eventService.createEvent(dto);
            return ResponseEntity.ok(newEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("행사 등록 실패");
        }
    }
    @GetMapping("/songs/events/period")
    public ResponseEntity<List<Event>> getEventsByPeriod(
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return ResponseEntity.ok(eventService.getEventsByPeriod(start, end));
    }
}