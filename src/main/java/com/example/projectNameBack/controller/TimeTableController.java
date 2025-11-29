package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.TimeTableDto;
import com.example.projectNameBack.repository.TimeTableRepository;
import com.example.projectNameBack.service.TimeTableService;
import com.example.projectNameBack.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
