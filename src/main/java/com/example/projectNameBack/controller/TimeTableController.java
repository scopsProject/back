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
            // userDetails.getUsername()은 학번(userID)일 수 있으니 확인 필요
            // 만약 토큰에 'name' claim이 있다면 그걸 써야 함.
            // 여기서는 간단히 UserDetails에서 꺼낸 ID로 유저를 찾는다고 가정.
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
    @PutMapping("/timetables/{id}") // 주소 주의 (/scops/timetable/{id} 라면 수정 필요)
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
    // 🔥 [추가] 다른 사람(친구) 시간표 조회 API
    // /timetables/user/{userId}
    @GetMapping("/timetables/user/{userId}")
    public ResponseEntity<List<TimeTableDto>> getFriendTimeTable(
            @PathVariable String userId
    ) {
        List<TimeTableDto> timeTables = timeTableService.getTimeTables(userId);
        return ResponseEntity.ok(timeTables);
    }
}
