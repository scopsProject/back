package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.EventRequestDto;
import com.example.projectNameBack.entity.Event;
import com.example.projectNameBack.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 🔥 import 확인
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j // 🔥 로그 기능 활성화
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    @Transactional
    public Event createEvent(EventRequestDto dto) {
        if (eventRepository.findByEventName(dto.getEventName()).isPresent()) {
            // 로그를 남기고 에러를 던지면 추적하기 좋습니다.
            log.warn("이미 존재하는 행사명 중복 시도: {}", dto.getEventName());
            throw new IllegalArgumentException("이미 존재하는 행사명입니다.");
        }

        Event event = new Event();
        event.setEventName(dto.getEventName());
        event.setCreatedDate(dto.getCreatedDate());
        event.setEndDate(dto.getEndDate());

        Event savedEvent = eventRepository.save(event);
        log.info("=== [EventService] DB 저장 완료: ID={}, 이름={} ===", savedEvent.getId(), savedEvent.getEventName());

        return savedEvent;
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByPeriod(LocalDate start, LocalDate end) {
        log.info("=== [EventService] Repository 조회 시작 (Start: {}, End: {}) ===", start, end);

        List<Event> events = eventRepository.findEventsByPeriod(start, end);

        log.info("=== [EventService] Repository 조회 완료 (건수: {}) ===", events.size());

        // 반복문 로그는 데이터가 너무 많으면 콘솔이 지저분해질 수 있으니 디버깅용으로만!
        if (!events.isEmpty()) {
            for (Event e : events) {
                log.debug(" -> 조회된 행사: {} ({} ~ {})", e.getEventName(), e.getCreatedDate(), e.getEndDate());
            }
        } else {
            log.info(" -> 해당 기간에 조회된 행사가 없습니다.");
        }

        return events;
    }
}