package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.ReservationDto;
import com.example.projectNameBack.dto.ReservationRequestDto;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    // 1. 곡 예약
    @PostMapping("/reservation")
    public ResponseEntity<String> reservationSong(@RequestBody ReservationRequestDto requestDto) {
        reservationService.reserveSong(requestDto);
        return ResponseEntity.ok("예약이 완료되었습니다.");
    }

    // 2. 주간 예약 조회
    @GetMapping("/by-week")
    public List<ReservationDto> getSongsByWeek(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return reservationService.getReservationsByDateRange(start, end);
    }

    // 3. 월간 예약 조회
    @GetMapping("/by-month")
    public List<ReservationDto> getMonthlyReservations(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return reservationService.getReservationsForMonth(start, end);
    }
    // 4. 예약 취소(삭제)
    // ReservationController.java

    // 4. 예약 취소(삭제)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal Object principal
    ) {
        String currentUserId = null;

        // Principal 타입에 따라 로그인 ID(userID, 학번 등) 추출
        if (principal instanceof User) {
            // Case A: 커스텀 User 엔티티인 경우 -> getUserID() 호출
            currentUserId = ((User) principal).getUserID();
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            // Case B: 스프링 시큐리티 기본 UserDetails인 경우
            // -> getUsername()이 로그인할 때 쓴 ID(학번)를 반환함
            currentUserId = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else {
            // Case C: String이나 기타 타입인 경우
            currentUserId = principal.toString();
        }

        // 서비스로 ID("2000")를 넘김
        reservationService.cancelReservation(id, currentUserId);

        return ResponseEntity.ok("예약이 취소되었습니다.");
    }
}