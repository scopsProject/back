package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.TimeTableDto;
import com.example.projectNameBack.service.TimeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TimeTableController {

    private final TimeTableService timeTableService;

    // 1. 시간표 추가
    @PostMapping("/scops/timetable")
    public ResponseEntity<?> addTimeTable(
            @RequestBody TimeTableDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 예외 처리는 Service -> GlobalExceptionHandler가 담당
        timeTableService.addTimeTable(userDetails.getUsername(), dto);
        return ResponseEntity.ok("시간표 추가 성공");
    }

    // 2. 내 시간표 조회
    @GetMapping("/scops/timetable")
    public ResponseEntity<List<TimeTableDto>> getMyTimeTable(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<TimeTableDto> timeTables = timeTableService.getTimeTables(userDetails.getUsername());
        return ResponseEntity.ok(timeTables);
    }

    // 3. 시간표 수정
    @PutMapping("/timetables/{id}")
    public ResponseEntity<?> updateTimeTable(
            @PathVariable Long id,
            @RequestBody TimeTableDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        timeTableService.updateTimeTable(id, userDetails.getUsername(), dto);
        return ResponseEntity.ok("수정 성공");
    }

    // 4. 시간표 삭제
    @DeleteMapping("/timetables/{id}")
    public ResponseEntity<?> deleteTimeTable(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        timeTableService.deleteTimeTable(id, userDetails.getUsername());
        return ResponseEntity.ok("삭제 성공");
    }

    // 5. 친구 시간표 조회
    @GetMapping("/timetables/user/{userId}")
    public ResponseEntity<List<TimeTableDto>> getFriendTimeTable(
            @PathVariable String userId
    ) {
        List<TimeTableDto> timeTables = timeTableService.getTimeTables(userId);
        return ResponseEntity.ok(timeTables);
    }
}