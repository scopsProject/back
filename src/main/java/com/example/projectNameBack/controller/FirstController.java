package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.LoginResponseDto;
import com.example.projectNameBack.dto.SaveUserLoginInfoDto;
// ... (다른 import)
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import com.example.projectNameBack.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.projectNameBack.util.JwtUtil; // ⬅️ JwtUtil 임포트

// ⬇️ ‼️ (중요) 인증된 사용자 정보를 가져오기 위한 import ‼️
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;


@RestController
public class FirstController {
    private final AuthService authService;
    private final UserLoginInfoRepository userLoginInfoRepository;

    // ⬇️ ‼️ 1. JwtUtil 필드 추가 ‼️
    private final JwtUtil jwtUtil;

    // ⬇️ ‼️ 2. 생성자에 JwtUtil 파라미터 추가 ‼️
    public FirstController(AuthService authService,
                           UserLoginInfoRepository userLoginInfoRepository,
                           JwtUtil jwtUtil){ // ⬅️ 주입받기
        this.authService = authService;
        this.userLoginInfoRepository = userLoginInfoRepository;
        this.jwtUtil = jwtUtil; // ⬅️ 초기화
    }

    // (로그인, 회원가입 메서드는 수정할 필요 없음 - public)
    @PostMapping("/scops/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData){
        // ... (기존 코드)
    }

    @PostMapping("/scops/userRegister")
    public ResponseEntity<Boolean> userRegister(@RequestBody SaveUserLoginInfoDto dto){
        // ... (기존 코드)
    }

    /*
     * ⬇️ ‼️ 3. (수정됨) deleteUser 메서드 ‼️
     * JwtAuthenticationFilter가 이미 토큰 검증을 완료했기 때문에,
     * 컨트롤러는 @AuthenticationPrincipal을 사용해 "현재 인증된 사용자"의
     * 정보(UserDetails)를 바로 받아올 수 있습니다.
     *
     * 굳이 헤더에서 토큰을 다시 파싱할 필요가 없습니다.
     */
    @DeleteMapping("/scops/deleteUser")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                // 필터에서 인증이 실패했거나 토큰이 없는 경우
                return ResponseEntity.status(401).body("인증 정보가 없습니다.");
            }

            // userDetails.getUsername()에 우리가 토큰 생성 시 넣었던 'userId'(학번)가 들어있습니다.
            String userID = userDetails.getUsername();

            authService.deleteUserCompletely(userID); // 서비스 호출

            return ResponseEntity.ok("회원 탈퇴 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버 오류");
        }
    }
}