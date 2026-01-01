package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.EventDto;
import com.example.projectNameBack.dto.EventRequestDto;
import com.example.projectNameBack.entity.Event;
import com.example.projectNameBack.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    // 1. 행사 생성 (반환 타입 변경: Event -> EventDto)
    @Transactional
    public EventDto createEvent(EventRequestDto dto) {
        if (eventRepository.findByEventName(dto.getEventName()).isPresent()) {
            log.warn("행사 생성 실패: 중복된 이름 ({})", dto.getEventName());
            throw new IllegalStateException("이미 존재하는 행사명입니다.");
        }

        Event event = dto.toEntity();
        Event savedEvent = eventRepository.save(event);

        log.info("행사 생성 완료: {} (ID: {}, 시작일: {})",
                savedEvent.getEventName(), savedEvent.getId(), savedEvent.getStartDate());

        // Entity를 DTO로 변환하여 반환
        return EventDto.fromEntity(savedEvent);
    }

    // 2. 곡등록 가능한 행사 이름 목록 조회 (String 리스트라 그대로 둠)
    @Transactional(readOnly = true)
    public List<String> getAvailableEventNames() {
        return eventRepository.findAvailableEventNames();
    }

    // 3. 모든 행사 이름 목록 조회 (String 리스트라 그대로 둠)
    @Transactional(readOnly = true)
    public List<String> getAllEventNames() {
        return eventRepository.findAll().stream()
                .map(Event::getEventName)
                .collect(Collectors.toList());
    }

    // 4. 기간별 행사 조회 (반환 타입 변경: List<Event> -> List<EventDto>)
    @Transactional(readOnly = true)
    public List<EventDto> getEventsByPeriod(LocalDate start, LocalDate end) {
        List<Event> events = eventRepository.findEventsByPeriod(start, end);
        log.info("기간별 행사 조회 성공: {}건 (기간: {} ~ {})", events.size(), start, end);

        // 리스트의 각 Entity를 DTO로 변환
        return events.stream()
                .map(EventDto::fromEntity)
                .collect(Collectors.toList());
    }
}