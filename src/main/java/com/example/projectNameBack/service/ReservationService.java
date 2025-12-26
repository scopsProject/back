package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.ReservationDto;
import com.example.projectNameBack.dto.ReservationRequestDto;
import com.example.projectNameBack.entity.Event;
import com.example.projectNameBack.entity.Reservation;
import com.example.projectNameBack.entity.SongRegister;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.repository.EventRepository;
import com.example.projectNameBack.repository.ReservationRepository;
import com.example.projectNameBack.repository.SongRegisterRepository;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final UserLoginInfoRepository userRepository;
    private final SongRegisterRepository songRepository;
    private final SseService sseService;

    // 1. 날짜별 예약 조회
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByDateRange(LocalDate start, LocalDate end) {
        List<Reservation> reservations = reservationRepository.findWithSessionsByDateRange(start, end);
        log.info("예약 조회 (기간: {} ~ {}): {}건", start, end, reservations.size());

        return reservations.stream()
                .map(ReservationDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 2. 월별 예약 조회
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsForMonth(LocalDate start, LocalDate end) {
        return reservationRepository.findByDateBetween(start, end).stream()
                .map(ReservationDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 3. 예약하기
    @Transactional
    public void reserveSong(ReservationRequestDto dto) {
        // A. 시간 검증
        validateReservationTime();

        // B. 중복 검증 (Guard Clause)
        if (!reservationRepository.findOverlappingReservations(dto.getDate(), dto.getStartTime(), dto.getEndTime()).isEmpty()) {
            throw new IllegalStateException("이미 예약된 시간대입니다.");
        }

        // C. 엔티티 조회 (Helper 메서드 활용 추천)
        Event event = eventRepository.findByEventName(dto.getEventName())
                .orElseThrow(() -> new IllegalArgumentException("행사를 찾을 수 없습니다: " + dto.getEventName()));

        User user = userRepository.findByUserName(dto.getUserName())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + dto.getUserName()));

        // D. 예약 생성 (빌더 패턴 권장)
        Reservation reservation = new Reservation();
        reservation.setEvent(event);
        reservation.setUser(user);
        reservation.setSingerName(dto.getSingerName());
        reservation.setSongName(dto.getSongName());
        reservation.setDate(dto.getDate());
        reservation.setStartTime(dto.getStartTime().withSecond(0).withNano(0));
        reservation.setEndTime(dto.getEndTime().withSecond(0).withNano(0));

        // 곡 등록 정보 연결 (Optional)
        if (dto.getSongRegisterId() != null) {
            SongRegister songRegister = songRepository.findById(dto.getSongRegisterId())
                    .orElseThrow(() -> new IllegalArgumentException("곡 정보를 찾을 수 없습니다."));
            reservation.setSongRegister(songRegister);
        }

        reservationRepository.save(reservation);
        log.info("예약 성공: {} - {}", user.getUserName(), dto.getSongName());

        // E. 알림 전송
        sseService.broadcast(dto);
    }

    // 4. 예약 시간 검증 (로직 단순화)
    private void validateReservationTime() {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek day = now.getDayOfWeek();
        int hour = now.getHour();

        // 예약 가능 여부 판단
        boolean isTuesdayOpen = (day == DayOfWeek.TUESDAY && hour >= 9);
        boolean isWednesdayOpen = (day == DayOfWeek.WEDNESDAY);
        boolean isThursdayOpen = (day == DayOfWeek.THURSDAY && hour < 19);

        if (!isTuesdayOpen && !isWednesdayOpen && !isThursdayOpen) {
            throw new IllegalStateException("예약 가능한 시간이 아닙니다. (화 09:00 ~ 목 19:00)");
        }
    }
}