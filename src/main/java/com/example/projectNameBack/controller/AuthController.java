package com.example.projectNameBack.controller;
import com.example.projectNameBack.dto.LoginRequestDto;
import com.example.projectNameBack.dto.LoginResponseDto;
import com.example.projectNameBack.dto.SaveUserLoginInfoDto;
import com.example.projectNameBack.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
        if (userDetails == null) {
            return ResponseEntity.status(401).body("인증 정보가 유효하지 않습니다.");
        }

        authService.deleteUserCompletely(userDetails.getUsername());
        return ResponseEntity.ok("회원 탈퇴 성공");
    }
}