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
import java.util.Map;
import java.util.Optional;

@RestController
public class FirstController {
    private final AuthService authService;
    private final UserLoginInfoRepository userLoginInfoRepository;
    public FirstController(AuthService authService, UserLoginInfoRepository userLoginInfoRepository){
        this.authService = authService;
        this.userLoginInfoRepository = userLoginInfoRepository;
    }

    @PostMapping("/scops/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData){
        String userID = loginData.get("userID");
        String password = loginData.get("password");

        LoginResponseDto response = authService.login(userID, password);

        if(response != null){
            return ResponseEntity.ok(response); // 로그인 성공: 사용자 정보 반환
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
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("토큰 없음");
            }

            String token = authHeader.substring(7);
            String userID = JwtUtil.getUserIdFromToken(token);
            if (userID == null) {
                return ResponseEntity.status(401).body("유효하지 않은 토큰");
            }

            authService.deleteUserCompletely(userID); // 서비스 호출

            return ResponseEntity.ok("회원 탈퇴 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버 오류");
        }
    }


}
