package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.TimeTableDto;
import com.example.projectNameBack.repository.TimeTableRepository;
import com.example.projectNameBack.service.TimeTableService;
import com.example.projectNameBack.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TimeTableController {
    private final JwtUtil jwtUtil;
    private final TimeTableService timeTableService;

    @PostMapping("/scops/timetable")
    public ResponseEntity<?> addTimeTable(
            @RequestBody TimeTableDto dto,
            @AuthenticationPrincipal UserDetails userDetails // 토큰에서 유저 정보 획득
    ) {
        try {
            timeTableService.addTimeTable(userDetails.getUsername(), dto);
            return ResponseEntity.ok("시간표 추가 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/scops/timetable")
    public ResponseEntity<List<TimeTableDto>> getMyTimeTable(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 서비스에서 DTO 리스트로 변환해서 받아옴
        List<TimeTableDto> timeTables = timeTableService.getTimeTables(userDetails.getUsername());
        return ResponseEntity.ok(timeTables);
    }
    // ...
    @PutMapping("/timetables/{id}")
    public ResponseEntity<?> updateTimeTable(
            @PathVariable Long id,
            @RequestBody TimeTableDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            timeTableService.updateTimeTable(id, userDetails.getUsername(), dto);
            return ResponseEntity.ok("수정 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/timetables/{id}")
    public ResponseEntity<?> deleteTimeTable(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            timeTableService.deleteTimeTable(id, userDetails.getUsername());
            return ResponseEntity.ok("삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/timetables/user/{userId}")
    public ResponseEntity<List<TimeTableDto>> getFriendTimeTable(
            @PathVariable String userId
    ) {
        List<TimeTableDto> timeTables = timeTableService.getTimeTables(userId);
        return ResponseEntity.ok(timeTables);
    }
}
