package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.SongRegisterDto;
import com.example.projectNameBack.dto.SongSessionDto;
import com.example.projectNameBack.entity.Event;
import com.example.projectNameBack.entity.SongRegister;
import com.example.projectNameBack.entity.SongSession;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.repository.EventRepository;
import com.example.projectNameBack.repository.SongRegisterRepository;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
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
public class SongService {

    private final SongRegisterRepository songRegisterRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final EventRepository eventRepository;

    // --- [조회 로직] ---

    // 1. 특정 행사의 곡 목록 조회
    @Transactional(readOnly = true)
    public List<SongRegisterDto> getSongsByEvent(String eventName) {
        if (eventName == null || eventName.trim().isEmpty()) {
            throw new IllegalArgumentException("행사 이름은 필수입니다.");
        }
        return songRegisterRepository.findByEvent_EventName(eventName).stream()
                .map(SongRegisterDto::from)
                .collect(Collectors.toList());
    }

    // 3. 곡 등록
    @Transactional
    public SongRegister saveSongRegister(SongRegisterDto dto) {
        SongRegister songRegister = new SongRegister();
        updateSongData(songRegister, dto); // 공통 로직 분리 활용

        // 날짜가 없으면 오늘 날짜로 설정
        if (dto.getDate() == null) {
            songRegister.setDate(LocalDate.now());
        }

        SongRegister saved = songRegisterRepository.save(songRegister);
        log.info("곡 신청 완료: {} (신청자: {})", saved.getSongName(), saved.getUserName());
        return saved;
    }

    // 4. 곡 수정
    @Transactional
    public void updateSong(Long id, SongRegisterDto dto) {
        SongRegister songRegister = songRegisterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 곡 신청을 찾을 수 없습니다. id=" + id));

        updateSongData(songRegister, dto);

        if (dto.getDate() != null) {
            songRegister.setDate(dto.getDate());
        }
        log.info("곡 수정 완료: ID={}", id);
    }

    // 5. 곡 삭제
    @Transactional
    public void deleteSong(Long id) {
        SongRegister songRegister = songRegisterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 곡 신청을 찾을 수 없습니다. id=" + id));

        songRegisterRepository.delete(songRegister);
        log.info("곡 삭제 완료: ID={}", id);
    }

    // 데이터 업데이트 공통 로직
    private void updateSongData(SongRegister songRegister, SongRegisterDto dto) {
        Event event = eventRepository.findByEventName(dto.getEventName())
                .orElseThrow(() -> new IllegalArgumentException("해당 행사를 찾을 수 없습니다: " + dto.getEventName()));

        songRegister.setEvent(event);
        songRegister.setSongName(dto.getSongName());
        songRegister.setSingerName(dto.getSingerName());
        songRegister.setUserName(dto.getUserName());

        if (dto.getDate() != null) {
            songRegister.setDate(dto.getDate());
        }

        processSessions(songRegister, dto.getSessions());
    }

    // 세션 처리 로직
    private void processSessions(SongRegister songRegister, List<SongSessionDto> sessionDtos) {
        // 기존 세션 클리어 (OrphanRemoval 동작 전제)
        songRegister.getSessions().clear();

        if (sessionDtos == null || sessionDtos.isEmpty()) {
            return;
        }

        for (SongSessionDto sessionDto : sessionDtos) {
            User player = userLoginInfoRepository.findByUserName(sessionDto.getPlayerName())
                    .orElseThrow(() -> new IllegalArgumentException("세션 참가자를 찾을 수 없습니다: " + sessionDto.getPlayerName()));

            SongSession session = new SongSession();
            session.setPlayer(player);
            session.setSessionType(sessionDto.getSessionType());

            // 양방향 연관관계 편의 메서드 호출
            songRegister.addSession(session);
        }
    }
}