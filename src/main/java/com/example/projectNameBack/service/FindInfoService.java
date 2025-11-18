package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.*;
import com.example.projectNameBack.entity.Reservation;
import com.example.projectNameBack.entity.SongRegister;
import com.example.projectNameBack.repository.ReservationRepository;
import com.example.projectNameBack.repository.SongRegisterRepository;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FindInfoService {
    private final SongRegisterRepository songRegisterRepository;
    private final ReservationRepository reservationRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;

    public FindInfoService(SongRegisterRepository songRegisterRepository, ReservationRepository reservationRepository, UserLoginInfoRepository userLoginInfoRepository) {
        this.songRegisterRepository = songRegisterRepository;
        this.reservationRepository = reservationRepository;
        this.userLoginInfoRepository = userLoginInfoRepository;
    }

    public List<SongRegisterDto> getSongsByEvent(String eventName) {
        List<SongRegister> songRegisters = songRegisterRepository.findByEventName(eventName);

        return songRegisters.stream()
                .map(songRegister -> {
                    SongRegisterDto dto = new SongRegisterDto();
                    dto.setId(songRegister.getId());
                    dto.setEventName(songRegister.getEventName());
                    dto.setSongName(songRegister.getSongName());
                    dto.setSingerName(songRegister.getSingerName());
                    dto.setUserName(songRegister.getUserName());
                    dto.setSessions(
                            songRegister.getSessions().stream()
                                    .map(session -> {
                                        SongSessionDto sessionDto = new SongSessionDto();
                                        sessionDto.setSessionType(session.getSessionType());
                                        sessionDto.setPlayerName(session.getPlayerName());
                                        return sessionDto;
                                    }).collect(Collectors.toList())
                    );
                    return dto;
                }).collect(Collectors.toList());
    }

    public List<ReservationDto> getReservationsByDateRange(LocalDate start, LocalDate end) {
        List<Reservation> reservations = reservationRepository.findWithSessionsByDateRange(start, end);
        System.out.println("조회된 예약 수(기간): " + reservations.size());

        return reservations.stream()
                .map(reservation -> ReservationDto.builder()
                        .eventName(reservation.getEventName())
                        .singerName(reservation.getSingerName())
                        .songName(reservation.getSongName())
                        .date(reservation.getDate())
                        .startTime(reservation.getStartTime())
                        .endTime(reservation.getEndTime())
                        .sessions(reservation.getSongRegister().getSessions().stream()
                                .map(session -> new SongSessionDto(session.getSessionType(), session.getPlayerName()))
                                .toList()
                        )
                        .build()
                ).toList();
    }



    public List<String> getEventNames() {
        return songRegisterRepository.findDistinctEventNames();
    }
    public List<SongRegister> findSongsByEventName(String eventName) {
        return songRegisterRepository.findByEventName(eventName);
    }
    public List<SongRegister> findSingerNameBySongName(String songName) {
        return songRegisterRepository.findBySongName(songName);
    }

    public List<UserInfoDto> getSessions() {
        return userLoginInfoRepository.findAllUsers()
                .stream()
                .map(u -> new UserInfoDto(u.getUserName(), u.getSession(), u.getUserYear()))
                .toList();
    }
    public List<ReservationDto> getReservationsForMonth(LocalDate start, LocalDate end) {
        return reservationRepository.findByDateBetween(start, end)
                .stream()
                .map(ReservationDto::fromEntity)
                .collect(Collectors.toList());
    }
    @Transactional
    public void reserveSong(ReservationRequestDto dto) {

        System.out.println("=== 예약 요청 DTO 확인 ===");
        System.out.println("eventName = " + dto.getEventName());
        System.out.println("singer = " + dto.getSingerName());
        System.out.println("title = " + dto.getSongName());
        System.out.println("date = " + dto.getDate());
        System.out.println("startTime = " + dto.getStartTime());
        System.out.println("endTime = " + dto.getEndTime());
        System.out.println("songRegisterId = " + dto.getSongRegisterId());


        /* 🔥 1. 동시 예약 방지 - DB 락 걸린 상태로 중복 조회 */
        List<Reservation> overlapped = reservationRepository.findOverlappingReservations(
                dto.getDate(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (!overlapped.isEmpty()) {
            throw new IllegalStateException(
                    "이미 예약된 시간대입니다. (" +
                            dto.getStartTime() + " ~ " + dto.getEndTime() + ")"
            );
        }


        /* 🔥 2. 예약 생성 */
        Reservation reservation = new Reservation();
        reservation.setEventName(dto.getEventName());
        reservation.setSingerName(dto.getSingerName());
        reservation.setSongName(dto.getSongName());
        reservation.setDate(dto.getDate());
        reservation.setStartTime(dto.getStartTime().withSecond(0).withNano(0));
        reservation.setEndTime(dto.getEndTime().withSecond(0).withNano(0));

        if (dto.getSongRegisterId() != null) {
            SongRegister songRegister = songRegisterRepository.findById(dto.getSongRegisterId())
                    .orElseThrow(() -> new RuntimeException("곡 등록 정보가 없습니다. id: " + dto.getSongRegisterId()));
            reservation.setSongRegister(songRegister);
        }


        /* 🔥 3. DB 저장 */
        reservationRepository.save(reservation);
    }

}
