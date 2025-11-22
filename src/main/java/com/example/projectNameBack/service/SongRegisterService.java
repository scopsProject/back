package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.SongRegisterDto;
import com.example.projectNameBack.dto.SongSessionDto;
import com.example.projectNameBack.entity.SongRegister;
import com.example.projectNameBack.entity.SongSession;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.repository.SongRegisterRepository;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SongRegisterService {

    private final SongRegisterRepository songRegisterRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;

    public SongRegisterService(SongRegisterRepository songRegisterRepository,
                               UserLoginInfoRepository userLoginInfoRepository) {
        this.songRegisterRepository = songRegisterRepository;
        this.userLoginInfoRepository = userLoginInfoRepository;
    }

    @Transactional
    public SongRegister saveSongRegister(SongRegisterDto dto) {
        SongRegister songRegister = new SongRegister();
        songRegister.setEventName(dto.getEventName());
        songRegister.setSongName(dto.getSongName());
        songRegister.setSingerName(dto.getSingerName());
        songRegister.setUserName(dto.getUserName());
        songRegister.setDate(dto.getDate());

        // 1️⃣ 세션 추가
        List<SongSessionDto> sessions = dto.getSessions();
        if (sessions != null) {
            sessions.forEach(s -> {
                SongSession session = new SongSession();
                session.setPlayerName(s.getPlayerName());
                session.setSessionType(s.getSessionType());
                session.setSongRegister(songRegister);
                songRegister.getSessions().add(session);
            });
        }

        // 2️⃣ 참여자 추가
        if (dto.getParticipantIds() != null) {
            Set<User> participants = new HashSet<>();
            dto.getParticipantIds().forEach(userId -> {
                User user = userLoginInfoRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
                participants.add(user);
            });
            songRegister.setParticipants(participants);
        }

        // 3️⃣ 저장
        return songRegisterRepository.save(songRegister);
    }

    @Transactional
    public void updateSong(Long id, SongRegisterDto dto) {
        SongRegister songRegister = songRegisterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노래 신청을 찾을 수 없습니다. id=" + id));

        // 메인 필드 업데이트
        songRegister.setEventName(dto.getEventName());
        songRegister.setSongName(dto.getSongName());
        songRegister.setSingerName(dto.getSingerName());
        songRegister.setUserName(dto.getUserName());
        songRegister.setDate(dto.getDate());

        // 기존 세션 삭제
        songRegister.getSessions().clear();

        // 새로운 세션 추가
        if (dto.getSessions() != null) {
            dto.getSessions().forEach(sessionDto -> {
                SongSession session = new SongSession();
                session.setPlayerName(sessionDto.getPlayerName());
                session.setSessionType(sessionDto.getSessionType());
                session.setSongRegister(songRegister);
                songRegister.getSessions().add(session);
            });
        }

        // 참여자 업데이트
        if (dto.getParticipantIds() != null) {
            Set<User> participants = new HashSet<>();
            dto.getParticipantIds().forEach(userId -> {
                User user = userLoginInfoRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
                participants.add(user);
            });
            songRegister.setParticipants(participants);
        } else {
            // 참여자 목록 비우기
            songRegister.getParticipants().clear();
        }

        songRegisterRepository.save(songRegister);
    }

    @Transactional
    public void deleteSong(Long id) {
        SongRegister songRegister = songRegisterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노래 신청을 찾을 수 없습니다. id=" + id));

        // ManyToMany 조인 테이블에서 관계 제거
        songRegister.getParticipants().clear();

        // 세션은 orphanRemoval = true 덕분에 자동 삭제
        songRegisterRepository.delete(songRegister);
    }
}
