package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.ReservationDto;
import com.example.projectNameBack.dto.ReservationRequestDto;
import com.example.projectNameBack.dto.SongRegisterDto;
import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.SongRegister;
import com.example.projectNameBack.service.AuthService;
import com.example.projectNameBack.service.ReservationService;
import com.example.projectNameBack.service.SongRegisterService;
import com.example.projectNameBack.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RequiredArgsConstructor
@RestController
public class SongRegisterController {

    private final SongRegisterService songRegisterService;
    private final SongService songService;
    private final ReservationService reservationService;
    private final AuthService authService;

    @PostMapping("/songs")
    public ResponseEntity<?> registerSong(@RequestBody SongRegisterDto dto) {
        try {
            SongRegister saved = songRegisterService.saveSongRegister(dto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("곡 등록 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 이벤트 이름으로 곡 조회
    @GetMapping("/songs/by-event")
    public List<SongRegisterDto> getSongsByEvent(@RequestParam String eventName) {
        return songService.getSongsByEvent(eventName);
    }

    // 등록된 이벤트 이름만 가져오기
    @GetMapping("/songs/events")
    public List<String> getEventNames() {
        return songService.getEventNames();
    }

    // 곡 예약
    @PostMapping("/songs/reservation")
    public ResponseEntity<?> reservationSong(@RequestBody ReservationRequestDto requestDto) {
        try {
            reservationService.reserveSong(requestDto);
            return ResponseEntity.ok("예약이 완료되었습니다.");
        } catch (IllegalStateException e) {
            // 🔥 시간 겹침 등 논리적 예약 실패 → 409 반환
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            // 🔥 서버 내부 문제
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예약 실패: " + e.getMessage());
        }
    }

    @GetMapping("/songs/by-week")
    public List<ReservationDto> getSongsByWeek(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        System.out.println("getSongsByWeek 호출됨, start=" + start + ", end=" + end);
        return reservationService.getReservationsByDateRange(start, end);
    }
    @GetMapping("/scops/sessions")
    public ResponseEntity<List<UserInfoDto>> getSessions(
            @AuthenticationPrincipal UserDetails userDetails // 🔥 토큰에서 '내 정보' 확인
    ) {
        // 1. 토큰에서 내 ID(학번) 추출
        String myId = userDetails.getUsername();

        // 2. 서비스에 "내 ID(myId) 빼고 친구들 리스트 줘" 라고 요청
        List<UserInfoDto> sessions = authService.getSessions(myId);

        return ResponseEntity.ok(sessions);
    }
    @GetMapping("/songs/by-month")
    public List<ReservationDto> getMonthlyReservations(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return reservationService.getReservationsForMonth(startDate, endDate);
    }
    @PutMapping("/songs/update/{id}")
    public ResponseEntity<?> updateSong(
            @PathVariable Long id,
            @RequestBody SongRegisterDto dto
    ) {
        try {
            songRegisterService.updateSong(id, dto);
            return ResponseEntity.ok("곡 정보가 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("수정 실패: " + e.getMessage());
        }
    }
    @DeleteMapping("/songs/delete/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable Long id) {
        try {
            songRegisterService.deleteSong(id);
            return ResponseEntity.ok("곡이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("삭제 실패: " + e.getMessage());
        }
    }
}
