package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.SongRegisterDto;
import com.example.projectNameBack.dto.SongSessionDto;
import com.example.projectNameBack.entity.SongRegister;
import com.example.projectNameBack.entity.SongSession;
import com.example.projectNameBack.repository.SongRegisterRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongRegisterService {

    private final SongRegisterRepository songRegisterRepository;

    public SongRegisterService(SongRegisterRepository songRegisterRepository) {
        this.songRegisterRepository = songRegisterRepository;
    }

    @Transactional
    public SongRegister saveSongRegister(SongRegisterDto dto) {
        SongRegister songRegister = new SongRegister();
        songRegister.setEventName(dto.getEventName());
        songRegister.setSongName(dto.getSongName());
        songRegister.setSingerName(dto.getSingerName());
        songRegister.setUserName(dto.getUserName());
        songRegister.setDate(dto.getDate());

        // 세션 엔티티 리스트 만들기
        // 세션 리스트가 null일 경우 대비
        List<SongSessionDto> sessions = dto.getSessions();
        if (sessions != null && !sessions.isEmpty()) {

            sessions.forEach(s -> {
                SongSession session = new SongSession();
                session.setPlayerName(s.getPlayerName());
                session.setSessionType(s.getSessionType());

                // 연관관계 설정
                session.setSongRegister(songRegister);
                songRegister.getSessions().add(session);
            });

        }


        return songRegisterRepository.save(songRegister);
    }
    @Transactional
    public void deleteSong(Long id) {
        SongRegister songRegister = songRegisterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노래 신청을 찾을 수 없습니다. id=" + id));

        songRegisterRepository.delete(songRegister);
    }
    @Transactional
    public void updateSong(Long id, SongRegisterDto dto) {

        // 1) 기존 데이터 조회
        SongRegister songRegister = songRegisterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노래 신청을 찾을 수 없습니다. id=" + id));

        // 2) 메인 필드 업데이트
        songRegister.setEventName(dto.getEventName());
        songRegister.setSongName(dto.getSongName());
        songRegister.setSingerName(dto.getSingerName());
        songRegister.setUserName(dto.getUserName());

        // 3) 기존 세션 모두 삭제
        songRegister.getSessions().clear();

        // 4) 새로운 세션 추가
        dto.getSessions().forEach(sessionDto -> {
            SongSession session = new SongSession();
            session.setSessionType(sessionDto.getSessionType());
            session.setPlayerName(sessionDto.getPlayerName());
            session.setSongRegister(songRegister);

            songRegister.getSessions().add(session);
        });

        // 5) 저장 후 반환
        songRegisterRepository.save(songRegister);
    }

}
