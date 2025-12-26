package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.SongRegisterDto;
import com.example.projectNameBack.entity.Event;
import com.example.projectNameBack.repository.EventRepository;
import com.example.projectNameBack.repository.SongRegisterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService { // 👈 이름 변경!

    private final SongRegisterRepository songRegisterRepository;
    private final EventRepository eventRepository;

    // 특정 행사의 곡 목록
    @Transactional(readOnly = true)
    public List<SongRegisterDto> getSongsByEvent(String eventName) {
        return songRegisterRepository.findByEvent_EventName(eventName).stream()
                .map(SongRegisterDto::from) // 🔥 DTO 변환 로직 위임
                .collect(Collectors.toList());
    }

    // 행사 이름 목록
    @Transactional(readOnly = true)
    public List<String> getEventNames() {
        return eventRepository.findAll().stream()
                .map(Event::getEventName)
                .collect(Collectors.toList());
    }
}
