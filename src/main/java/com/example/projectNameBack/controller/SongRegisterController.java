package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.ReservationDto;
import com.example.projectNameBack.dto.ReservationRequestDto;
import com.example.projectNameBack.dto.SongRegisterDto;
import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.SongRegister;
import com.example.projectNameBack.service.FindInfoService;
import com.example.projectNameBack.service.SongRegisterService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class SongRegisterController {

    private final SongRegisterService songRegisterService;
    private final FindInfoService findInfoService;

    public SongRegisterController(SongRegisterService songRegisterService, FindInfoService findInfoService) {
        this.songRegisterService = songRegisterService;
        this.findInfoService = findInfoService;
    }

    @PostMapping
    public ResponseEntity<?> registerSong(@RequestBody SongRegisterDto dto) {
        try {
            SongRegister saved = songRegisterService.saveSongRegister(dto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("곡 등록 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 이벤트 이름으로 곡 조회
    @GetMapping("/api/songs/by-event")
    public List<SongRegisterDto> getSongsByEvent(@RequestParam String eventName) {
        return findInfoService.getSongsByEvent(eventName);
    }

    // 등록된 이벤트 이름만 가져오기
    @GetMapping("/api/songs/events")
    public List<String> getEventNames() {
        return findInfoService.getEventNames();
    }

    // 곡 예약
    @PostMapping("/api/songs/reservation")
    public ResponseEntity<?> reservationSong(@RequestBody ReservationRequestDto requestDto) {
        try {
            findInfoService.reserveSong(requestDto);
            return ResponseEntity.ok("예약이 완료되었습니다.");
        } catch (Exception e) {
            return new ResponseEntity<>("예약 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/api/songs/by-week")
    public List<ReservationDto> getSongsByWeek(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        System.out.println("getSongsByWeek 호출됨, start=" + start + ", end=" + end);
        return findInfoService.getReservationsByDateRange(start, end);
    }
    @GetMapping("/scops/sessions")
    public List<UserInfoDto> getSessions() {
        return findInfoService.getSessions();
    }
    @GetMapping("/api/by-month")
    public List<ReservationDto> getMonthlyReservations(@RequestParam String start, @RequestParam String end){
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return findInfoService.getReservationsForMonth(startDate, endDate);
    }
}
