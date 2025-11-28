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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SongRegisterService {

    private final SongRegisterRepository songRegisterRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final EventRepository eventRepository;

    public SongRegisterService(SongRegisterRepository songRegisterRepository,
                               UserLoginInfoRepository userLoginInfoRepository, EventRepository eventRepository) {
        this.songRegisterRepository = songRegisterRepository;
        this.userLoginInfoRepository = userLoginInfoRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public SongRegister saveSongRegister(SongRegisterDto dto) {
        SongRegister songRegister = new SongRegister();

        // 1. Event 객체 찾아서 설정 (이미 잘 되어있음)
        Event event = eventRepository.findByEventName(dto.getEventName())
                .orElseThrow(() -> new IllegalArgumentException("해당 행사를 찾을 수 없습니다: " + dto.getEventName()));
        songRegister.setEvent(event);

        songRegister.setSongName(dto.getSongName());
        songRegister.setSingerName(dto.getSingerName());
        songRegister.setUserName(dto.getUserName());

        if (dto.getDate() != null) {
            songRegister.setDate(dto.getDate());
        } else {
            songRegister.setDate(LocalDate.now());
        }

        // 세션 추가 로직
        List<SongSessionDto> sessions = dto.getSessions();
        if (sessions != null) {
            sessions.forEach(s -> {
                User player = userLoginInfoRepository.findByUserName(s.getPlayerName())
                        .orElseThrow(() -> new IllegalArgumentException("해당 이름의 유저를 찾을 수 없습니다: " + s.getPlayerName()));

                SongSession session = new SongSession();
                session.setPlayer(player);
                session.setSessionType(s.getSessionType());
                session.setSongRegister(songRegister);
                songRegister.getSessions().add(session);
            });
        }

        // 참여자 추가 로직
        if (dto.getParticipantIds() != null) {
            Set<User> participants = new HashSet<>();
            dto.getParticipantIds().forEach(userId -> {
                User user = userLoginInfoRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
                participants.add(user);
            });
            songRegister.setParticipants(participants);
        }

        return songRegisterRepository.save(songRegister);
    }

    @Transactional
    public void updateSong(Long id, SongRegisterDto dto) {
        SongRegister songRegister = songRegisterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노래 신청을 찾을 수 없습니다. id=" + id));

        Event event = eventRepository.findByEventName(dto.getEventName())
                .orElseThrow(() -> new IllegalArgumentException("해당 행사를 찾을 수 없습니다: " + dto.getEventName()));
        songRegister.setEvent(event);

        songRegister.setSongName(dto.getSongName());
        songRegister.setSingerName(dto.getSingerName());
        songRegister.setUserName(dto.getUserName());
        songRegister.setDate(dto.getDate());

        // 기존 세션 삭제
        songRegister.getSessions().clear();

        // 새로운 세션 추가
        if (dto.getSessions() != null) {
            dto.getSessions().forEach(sessionDto -> {
                User player = userLoginInfoRepository.findByUserName(sessionDto.getPlayerName())
                        .orElseThrow(() -> new IllegalArgumentException("해당 이름의 유저를 찾을 수 없습니다: " + sessionDto.getPlayerName()));

                SongSession session = new SongSession();
                session.setPlayer(player);
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
            songRegister.getParticipants().clear();
        }

        songRegisterRepository.save(songRegister);
    }

    @Transactional
    public void deleteSong(Long id) {
        SongRegister songRegister = songRegisterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노래 신청을 찾을 수 없습니다. id=" + id));
        songRegister.getParticipants().clear();
        songRegisterRepository.delete(songRegister);
    }
}