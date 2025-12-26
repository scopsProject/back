package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.ReservationDto;
import com.example.projectNameBack.dto.ReservationRequestDto;
import com.example.projectNameBack.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}