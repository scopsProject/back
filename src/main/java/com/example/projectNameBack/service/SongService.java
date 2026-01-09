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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
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
        songRegister.setUserName(dto.getUserName());
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
    public void deleteSong(Long id, String currentUserId) {
        // 1. 삭제할 곡 정보 조회
        SongRegister songRegister = songRegisterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("곡 정보를 찾을 수 없습니다."));

        // 2. 현재 로그인한 사용자의 정보를 ID(학번)로 조회하여 '실명'을 가져옴
        User currentUser = userLoginInfoRepository.findByUserID(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다."));

        String currentUserName = currentUser.getUserName();

        // 3. 곡에 저장된 등록자 이름 가져오기
        String savedOwnerName = songRegister.getUserName();

        // 4. 이름 비교 검증
        if (!savedOwnerName.equals(currentUserName)) {
            throw new AccessDeniedException("본인이 등록한 곡만 삭제할 수 있습니다.");
        }

        // 5. 삭제 진행
        songRegisterRepository.delete(songRegister);

        log.info("곡 삭제 완료 - 곡ID: {}, 요청자명: {}", id, currentUserName);
    }

    // 데이터 업데이트 공통 로직
    private void updateSongData(SongRegister songRegister, SongRegisterDto dto) {
        Event event = eventRepository.findByEventName(dto.getEventName())
                .orElseThrow(() -> new IllegalArgumentException("해당 행사를 찾을 수 없습니다: " + dto.getEventName()));

        songRegister.setEvent(event);
        songRegister.setSongName(dto.getSongName());
        songRegister.setSingerName(dto.getSingerName());

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