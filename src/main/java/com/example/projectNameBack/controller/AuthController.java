package com.example.projectNameBack.controller;
import com.example.projectNameBack.dto.LoginRequestDto;
import com.example.projectNameBack.dto.LoginResponseDto;
import com.example.projectNameBack.dto.SaveUserLoginInfoDto;
import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scops")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/scops/login";
    }

    // 1. 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request.getUserID(), request.getPassword()));
    }

    // 2. 회원가입
    @PostMapping("/userRegister")
    public ResponseEntity<String> register(@RequestBody SaveUserLoginInfoDto dto) {
        authService.saveUserInfo(dto);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 3. 회원 탈퇴
    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        authService.deleteUserCompletely(userDetails);

        return ResponseEntity.ok("회원 탈퇴 성공");
    }
    // 세션(친구 목록 등) 조회
    @GetMapping("/sessions")
    public ResponseEntity<List<UserInfoDto>> getSessions(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 1. 토큰에서 내 ID(학번) 추출
        String myId = userDetails.getUsername();

        // 2. 서비스에 "내 ID(myId) 빼고 친구들 리스트 줘" 라고 요청
        List<UserInfoDto> sessions = authService.getSessions(myId);

        return ResponseEntity.ok(sessions);
    }
}