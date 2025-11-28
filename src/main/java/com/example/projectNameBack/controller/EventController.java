package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.EventRequestDto;
import com.example.projectNameBack.entity.Event;
import com.example.projectNameBack.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}