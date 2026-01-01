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
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal Object principal
    ) {
        System.out.println("=== 삭제 요청 도착! ID: " + id + " ===");
        System.out.println("=== Principal 타입: " + principal.getClass().getName() + " ===");
        System.out.println("=== Principal 값: " + principal + " ===");
        String currentUserName = null;

        // 2. Principal 타입에 따라 이름 꺼내기
        if (principal instanceof User) {
            // Case A: Principal이 User 엔티티인 경우 (CustomUserDetailsService 사용 시)
            currentUserName = ((User) principal).getUserName();
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            // Case B: 일반 UserDetails인 경우 (주의: getUsername()은 보통 ID입니다. 이름이 필요하면 확인 필요)
            // UserDetails에는 실명(userName) 필드가 없을 수 있으므로 확인이 필요합니다.
            // 만약 UserDetails 구현체에 실명이 없다면 ID를 넘겨서 Service에서 다시 조회해야 합니다.
            currentUserName = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else {
            // Case C: String으로 들어오는 경우
            currentUserName = principal.toString();
        }

        System.out.println("=== 추출된 사용자 이름: " + currentUserName + " ===");

        reservationService.cancelReservation(id, currentUserName);
        return ResponseEntity.ok("예약이 취소되었습니다.");
    }
}