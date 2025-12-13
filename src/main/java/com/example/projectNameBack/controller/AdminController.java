package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/scops/admin") // ⬅️ URL 경로를 프로젝트 규칙에 맞게 설정하세요 (예: /scops/admin)
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 1. 승인 대기중인 회원 목록 조회
    @GetMapping("/pending-users")
    public ResponseEntity<List<UserInfoDto>> getPendingUsers() {
        return ResponseEntity.ok(adminService.getPendingUsers());
    }

    // 2. 회원 가입 승인 처리
    @PostMapping("/approve/{userID}")
    public ResponseEntity<String> approveUser(@PathVariable String userID) { // ⬅️ Long -> String 변경
        adminService.approveUser(userID);
        return ResponseEntity.ok("승인 완료: " + userID);
    }
    // 3. 회원 가입 거절 처리
    @DeleteMapping("/reject/{userID}")
    public ResponseEntity<String> rejectUser(@PathVariable String userID) {
        log.info("🎉 [Controller Reached] DELETE Request for UserID: {}", userID);
        adminService.rejectUser(userID);
        return ResponseEntity.ok("거절(삭제) 완료: " + userID);
    }
}