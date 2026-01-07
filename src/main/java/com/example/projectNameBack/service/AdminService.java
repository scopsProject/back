package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.entity.UserRole;
import com.example.projectNameBack.entity.UserStatus;
import com.example.projectNameBack.exception.UserNotFoundException;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserLoginInfoRepository userLoginInfoRepository;

    public AdminService(UserLoginInfoRepository userLoginInfoRepository) {
        this.userLoginInfoRepository = userLoginInfoRepository;
    }

    // 1. 대기중인(PENDING) 회원 목록 가져오기
    public List<UserInfoDto> getPendingUsers() {
        List<User> users = userLoginInfoRepository.findByStatus(UserStatus.PENDING);
        return users.stream()
                .map(UserInfoDto::from)
                .collect(Collectors.toList());
    }

    // 2. 회원 승인하기
    @Transactional
    public void approveUser(String userID) {
        User user = findUserByUserID(userID);
        user.setStatus(UserStatus.ACTIVE);
    }

    // 3. 회원가입 거절 및 삭제
    @Transactional
    public void rejectUser(String userID) {
        User user = findUserByUserID(userID);
        userLoginInfoRepository.delete(user);
    }

    // 4. 권한 변경
    @Transactional
    public void updateUserRole(String userID, String newRoleStr) {
        User user = findUserByUserID(userID);
        try {
            UserRole newRole = UserRole.valueOf(newRoleStr.toUpperCase());
            user.setRole(newRole);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("존재하지 않는 권한입니다: " + newRoleStr);
        }
    }

    // 5. 승인된(ACTIVE) 회원 목록 가져오기
    @Transactional(readOnly = true)
    public List<UserInfoDto> getActiveUsers(String myId) {
        return userLoginInfoRepository.findByUserIDNotAndStatus(myId, UserStatus.ACTIVE).stream()
                .map(UserInfoDto::from)
                .collect(Collectors.toList());
    }

    // 6. 강제 탈퇴 로직 (수정됨)
    @Transactional
    public void forceWithdrawal(String userId) {
        // 1. 유저 찾기
        User user = findUserByUserID(userId);

        // 2. [변경] 삭제 대신 상태를 '탈퇴'로 변경
        user.setStatus(UserStatus.WITHDRAWN);

        // 보안을 위해 비밀번호나 개인정보를 비워줄 수도 있음
        // user.setUserPassword("");
    }

    private User findUserByUserID(String userID) {
        return userLoginInfoRepository.findByUserID(userID)
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다: " + userID));
    }
}