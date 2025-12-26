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
        User user = findUserByUserID(userID); // 헬퍼 메서드 호출
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
            // 문자열을 Enum으로 변환
            UserRole newRole = UserRole.valueOf(newRoleStr.toUpperCase());
            user.setRole(newRole);
        } catch (IllegalArgumentException e) {
            // 잘못된 권한 문자열이 들어오면 400 Bad Request (GlobalExceptionHandler가 처리)
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

    private User findUserByUserID(String userID) {
        return userLoginInfoRepository.findByUserID(userID)
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다: " + userID));
    }
}