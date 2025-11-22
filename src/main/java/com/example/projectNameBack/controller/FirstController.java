package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.LoginResponseDto;
import com.example.projectNameBack.dto.SaveUserLoginInfoDto;
import com.example.projectNameBack.dto.SongRegisterDto;
import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import com.example.projectNameBack.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.projectNameBack.util.JwtUtil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;
import java.util.Optional;
import com.example.projectNameBack.util.JwtUtil; // ⬅️ JwtUtil 임포트

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
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/scops/login";
    }

    @PostMapping("/scops/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData){
        String userID = loginData.get("userID");
        String password = loginData.get("password");

        LoginResponseDto response = authService.login(userID, password);

        if(response != null){
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("로그인 실패");
        }
    }


    @PostMapping("/scops/userRegister")
    public ResponseEntity<Boolean> userRegister(@RequestBody SaveUserLoginInfoDto dto){
        try {
            User saved = authService.saveUserInfo(dto);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
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
