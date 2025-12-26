package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.RoleUpdateRequest;
import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/scops/admin")
public class AdminController {

    private final AdminService adminService;

    // 1. 승인 대기중인 회원 목록 조회
    @GetMapping("/pending-users")
    public ResponseEntity<List<UserInfoDto>> getPendingUsers() {
        return ResponseEntity.ok(adminService.getPendingUsers());
    }

    // 2. 회원 가입 승인 처리
    @PostMapping("/approve/{userID}")
    public ResponseEntity<String> approveUser(@PathVariable String userID) {
        adminService.approveUser(userID);
        return ResponseEntity.ok("승인 완료: " + userID);
    }
    // 3. 회원 가입 거절 처리
    @DeleteMapping("/reject/{userID}")
    public ResponseEntity<String> rejectUser(@PathVariable String userID) {
        adminService.rejectUser(userID);
        return ResponseEntity.ok("거절(삭제) 완료: " + userID);
    }
    @GetMapping("/active-users")
    public ResponseEntity<List<UserInfoDto>> getActiveUsers(
            @AuthenticationPrincipal UserDetails userDetails // 1. 내 정보(관리자 ID) 가져오기
    ) {
        String myId = userDetails.getUsername();

        return ResponseEntity.ok(adminService.getActiveUsers(myId));
    }

    // 5. 권한 변경 API
    @PatchMapping("/update-role/{userID}")
    public ResponseEntity<String> updateUserRole(@PathVariable String userID, @RequestBody RoleUpdateRequest request) {
        adminService.updateUserRole(userID, request.getRole());
        return ResponseEntity.ok("권한 변경 완료");
    }
}