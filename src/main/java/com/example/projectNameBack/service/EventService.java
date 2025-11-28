package com.example.projectNameBack.service;
import com.example.projectNameBack.dto.EventRequestDto;
import com.example.projectNameBack.entity.Event;
import com.example.projectNameBack.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    @Transactional
    public Event createEvent(EventRequestDto dto) {
        // 중복 이름 체크 (선택사항)
        if (eventRepository.findByEventName(dto.getEventName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 행사명입니다.");
        }

        Event event = new Event();
        event.setEventName(dto.getEventName());
        event.setCreatedDate(dto.getCreatedDate());
        event.setEndDate(dto.getEndDate());

        return eventRepository.save(event);
    }
    @Transactional(readOnly = true)
    public List<Event> getEventsByPeriod(LocalDate start, LocalDate end) {
        return eventRepository.findEventsByPeriod(start, end);
    }
}