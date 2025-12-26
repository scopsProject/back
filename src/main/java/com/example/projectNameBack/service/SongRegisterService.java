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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Slf4j
@RequiredArgsConstructor
@Service
public class SongRegisterService {

    private final SongRegisterRepository songRegisterRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final EventRepository eventRepository;

    @Transactional
    public SongRegister saveSongRegister(SongRegisterDto dto) {
        // 1. 기본 정보 매핑
        SongRegister songRegister = new SongRegister();

        // 2. 공통 필드 업데이트 (저장/수정 로직 공유)
        updateSongData(songRegister, dto);

        // 3. 날짜 설정 (없으면 오늘 날짜)
        if (dto.getDate() == null) {
            songRegister.setDate(LocalDate.now());
        }

        SongRegister saved = songRegisterRepository.save(songRegister);
        log.info("곡 신청 완료: {} (신청자: {})", saved.getSongName(), saved.getUserName());
        return saved;
    }

    @Transactional
    public void updateSong(Long id, SongRegisterDto dto) {
        SongRegister songRegister = songRegisterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 곡 신청을 찾을 수 없습니다. id=" + id));

        // 1. 공통 필드 및 세션 업데이트
        updateSongData(songRegister, dto);

        // 수정의 경우 날짜가 명시되어 있다면 변경 (선택 사항)
        if (dto.getDate() != null) {
            songRegister.setDate(dto.getDate());
        }

        // JPA Dirty Checking으로 인해 save() 호출 없어도 되지만, 명시적으로 해도 무방함
        log.info("곡 수정 완료: ID={}", id);
    }

    @Transactional
    public void deleteSong(Long id) {
        SongRegister songRegister = songRegisterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 곡 신청을 찾을 수 없습니다. id=" + id));

        songRegisterRepository.delete(songRegister);
        log.info("곡 삭제 완료: ID={}", id);
    }

    private void updateSongData(SongRegister songRegister, SongRegisterDto dto) {
        // 1. 행사 연결
        Event event = eventRepository.findByEventName(dto.getEventName())
                .orElseThrow(() -> new IllegalArgumentException("해당 행사를 찾을 수 없습니다: " + dto.getEventName()));
        songRegister.setEvent(event);

        // 2. 기본 정보 설정
        songRegister.setSongName(dto.getSongName());
        songRegister.setSingerName(dto.getSingerName());
        songRegister.setUserName(dto.getUserName());
        if (dto.getDate() != null) {
            songRegister.setDate(dto.getDate());
        }

        // 3. 세션 정보 업데이트 (싹 지우고 다시 추가하는 방식)
        processSessions(songRegister, dto.getSessions());
    }

    private void processSessions(SongRegister songRegister, List<SongSessionDto> sessionDtos) {
        // 기존 세션 모두 제거 (orphanRemoval=true 덕분에 DB에서도 삭제됨)
        songRegister.getSessions().clear();

        if (sessionDtos == null || sessionDtos.isEmpty()) {
            return;
        }

        for (SongSessionDto sessionDto : sessionDtos) {
            User player = userLoginInfoRepository.findByUserName(sessionDto.getPlayerName())
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + sessionDto.getPlayerName()));

            SongSession session = new SongSession();
            session.setPlayer(player);
            session.setSessionType(sessionDto.getSessionType());

            songRegister.addSession(session);
        }
    }
}