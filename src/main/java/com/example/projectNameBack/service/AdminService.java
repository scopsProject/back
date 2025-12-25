package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.entity.UserRole;
import com.example.projectNameBack.entity.UserStatus;
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

    // 1. 대기중인(PENDING) 회원 목록 가져오기 -> UserInfoDto 리스트 반환
    public List<UserInfoDto> getPendingUsers() {
        List<User> users = userLoginInfoRepository.findByStatus(UserStatus.PENDING);

        // User 엔티티 리스트를 UserInfoDto 리스트로 변환
        return users.stream()
                .map(user -> new UserInfoDto(
                        user.getUserID(),
                        user.getUserName(),
                        user.getSession(),
                        user.getRole(),
                        user.getUserYear(),
                        user.getStatus().name() // Enum을 String으로 변환
                ))
                .collect(Collectors.toList());
    }

    // 2. 회원 승인하기
    @Transactional
    public void approveUser(String userID) {
        User user = userLoginInfoRepository.findByUserID(userID)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userID));

        user.setStatus(UserStatus.ACTIVE);
    }
    // 3. 회원가입 거절한거 삭제
    @Transactional
    public void rejectUser(String userID) {
        User user = userLoginInfoRepository.findByUserID(userID)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userID));

        userLoginInfoRepository.delete(user); // Repository에 delete 메서드는 기본적으로 있습니다.
    }
    @Transactional
    public void updateUserRole(String userID, String newRoleStr) {
        User user = userLoginInfoRepository.findByUserID(userID)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userID));

        try {
            UserRole newRole = UserRole.valueOf(newRoleStr.toUpperCase());
            user.setRole(newRole);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("존재하지 않는 권한입니다: " + newRoleStr);
        }
    }
    // 4. 승인된(ACTIVE) 회원 목록 가져오기 (직위 변경용)
    public List<UserInfoDto> getActiveUsers(String myId) {
        List<User> users = userLoginInfoRepository.findByUserIDNotAndStatus(myId, UserStatus.ACTIVE);

        return users.stream()
                .map(user -> new UserInfoDto(
                        user.getUserID(),
                        user.getUserName(),
                        user.getSession(),
                        user.getRole(),
                        user.getUserYear(),
                        user.getStatus().name()
                ))
                .collect(Collectors.toList());
    }
}