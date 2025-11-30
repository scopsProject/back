package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.*;
import com.example.projectNameBack.entity.*;
import com.example.projectNameBack.repository.EventRepository; // 🔥 추가
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
    private final SseService sseService;
    private final EventRepository eventRepository; // 🔥 추가: Event 객체 찾기용

    // 생성자에 EventRepository 추가
    public FindInfoService(SongRegisterRepository songRegisterRepository,
                           ReservationRepository reservationRepository,
                           UserLoginInfoRepository userLoginInfoRepository,
                           SseService sseService,
                           EventRepository eventRepository) {
        this.songRegisterRepository = songRegisterRepository;
        this.reservationRepository = reservationRepository;
        this.userLoginInfoRepository = userLoginInfoRepository;
        this.sseService = sseService;
        this.eventRepository = eventRepository;
    }

    // 1. 특정 행사의 곡 목록 가져오기
    public List<SongRegisterDto> getSongsByEvent(String eventName) {
        List<SongRegister> songRegisters = songRegisterRepository.findByEvent_EventName(eventName);

        return songRegisters.stream()
                .map(songRegister -> {
                    SongRegisterDto dto = new SongRegisterDto();
                    dto.setId(songRegister.getId());

                    // 🔥 [수정] Event 객체에서 이름 꺼내기
                    if (songRegister.getEvent() != null) {
                        dto.setEventName(songRegister.getEvent().getEventName());
                    }

                    dto.setSongName(songRegister.getSongName());
                    dto.setSingerName(songRegister.getSingerName());
                    dto.setUserName(songRegister.getUserName());
                    dto.setSessions(
                            songRegister.getSessions().stream()
                                    .map(session -> {
                                        SongSessionDto sessionDto = new SongSessionDto();
                                        sessionDto.setSessionType(session.getSessionType());

                                        if (session.getPlayer() != null) {
                                            sessionDto.setPlayerName(session.getPlayer().getUserName());
                                        } else {
                                            sessionDto.setPlayerName("알 수 없음(탈퇴)");
                                        }

                                        return sessionDto;
                                    }).collect(Collectors.toList())
                    );
                    return dto;
                }).collect(Collectors.toList());
    }

    // 2. 날짜별 예약 정보 가져오기
    public List<ReservationDto> getReservationsByDateRange(LocalDate start, LocalDate end) {
        List<Reservation> reservations = reservationRepository.findWithSessionsByDateRange(start, end);
        System.out.println("조회된 예약 수(기간): " + reservations.size());

        return reservations.stream()
                .map(reservation -> ReservationDto.builder()
                        // 🔥 [수정] Event 객체에서 이름 꺼내기
                        .eventName(reservation.getEvent() != null ? reservation.getEvent().getEventName() : "Unknown")

                        .singerName(reservation.getSingerName())
                        .songName(reservation.getSongName())
                        .date(reservation.getDate())
                        .startTime(reservation.getStartTime())
                        .endTime(reservation.getEndTime())
                        .sessions(reservation.getSongRegister() != null ?
                                reservation.getSongRegister().getSessions().stream()
                                        .map(session -> new SongSessionDto(
                                                session.getSessionType(),
                                                session.getPlayer() != null ? session.getPlayer().getUserName() : "Unknown"
                                        ))
                                        .toList() : List.of()
                        )
                        .build()
                ).toList();
    }

    // 3. 행사 이름 목록 가져오기 (EventRepository 사용)
    public List<String> getEventNames() {
        // 🔥 [수정] SongRegister가 아니라 Event 테이블에서 직접 모든 행사 이름을 가져옵니다.
        return eventRepository.findAll().stream()
                .map(Event::getEventName)
                .collect(Collectors.toList());
    }

    // 4. (참고) 단순 조회용 메소드들
    public List<SongRegister> findSongsByEventName(String eventName) {
        // JPA Naming Rule에 따라 findByEvent_EventName 권장
        return songRegisterRepository.findByEvent_EventName(eventName);
    }
    public List<SongRegister> findSingerNameBySongName(String songName) {
        return songRegisterRepository.findBySongName(songName);
    }

    public List<UserInfoDto> getSessions(String myUserId) {
        // 1. DB에 있는 '모든' 회원 가져오기 (접속 여부와 상관없음!)
        List<User> allUsers = userLoginInfoRepository.findAll();

        return allUsers.stream()
                // 2. 🔥 [필터링] 내 아이디(myUserId)와 '다른' 사람만 남기기
                .filter(u -> !u.getUserID().equals(myUserId))

                // 3. DTO로 변환
                .map(u -> new UserInfoDto(
                        u.getUserID(), // 🔥 학번도 같이 담아서 보냄
                        u.getUserName(),
                        u.getSession(),
                        u.getRole(),
                        u.getUserYear()
                ))
                .toList();
    }

    // 5. 월별 예약 조회
    public List<ReservationDto> getReservationsForMonth(LocalDate start, LocalDate end) {
        return reservationRepository.findByDateBetween(start, end)
                .stream()
                .map(ReservationDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 6. 예약하기 (가장 중요!)
    @Transactional
    public void reserveSong(ReservationRequestDto dto) {

        System.out.println("=== 예약 요청 DTO 확인 ===");
        // ... 로그 생략 ...

        List<Reservation> overlapped = reservationRepository.findOverlappingReservations(
                dto.getDate(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (!overlapped.isEmpty()) {
            throw new IllegalStateException(
                    "이미 예약된 시간대입니다. (" + dto.getStartTime() + " ~ " + dto.getEndTime() + ")"
            );
        }

        Reservation reservation = new Reservation();

        // 🔥 [수정] 행사 이름으로 Event 객체 찾아서 저장하기
        Event event = eventRepository.findByEventName(dto.getEventName())
                .orElseThrow(() -> new IllegalArgumentException("해당 행사를 찾을 수 없습니다: " + dto.getEventName()));
        reservation.setEvent(event); // ✅ 객체 저장

        reservation.setSingerName(dto.getSingerName());
        reservation.setSongName(dto.getSongName());
        reservation.setDate(dto.getDate());
        reservation.setStartTime(dto.getStartTime().withSecond(0).withNano(0));
        reservation.setEndTime(dto.getEndTime().withSecond(0).withNano(0));

        // 사용자 저장 (이전 턴에서 수정한 부분)
        if (dto.getUserName() != null) {
            User user = userLoginInfoRepository.findByUserName(dto.getUserName())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + dto.getUserName()));
            reservation.setUser(user);
        } else {
            throw new IllegalArgumentException("로그인 정보(사용자 이름)가 없습니다.");
        }

        if (dto.getSongRegisterId() != null) {
            SongRegister songRegister = songRegisterRepository.findById(dto.getSongRegisterId())
                    .orElseThrow(() -> new RuntimeException("곡 등록 정보가 없습니다. id: " + dto.getSongRegisterId()));
            reservation.setSongRegister(songRegister);
        }

        reservationRepository.save(reservation);
        sseService.broadcast(dto);
    }
}