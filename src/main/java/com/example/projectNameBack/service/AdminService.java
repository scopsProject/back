package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.entity.UserRole;
import com.example.projectNameBack.entity.UserStatus;
import com.example.projectNameBack.exception.UserNotFoundException;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

        log.info("관리자 회원 승인 완료 - 대상: {} ({})", user.getUserName(), userID);
    }

    // 3. 회원가입 거절 및 삭제
    @Transactional
    public void rejectUser(String userID) {
        User user = findUserByUserID(userID);
        String userName = user.getUserName();

        userLoginInfoRepository.delete(user);

        log.info("관리자 가입 거절(삭제) 완료 - 대상: {} ({})", userName, userID);
    }

    // 4. 권한 변경
    @Transactional
    public void updateUserRole(String userID, String newRoleStr) {
        User user = findUserByUserID(userID);
        try {
            UserRole oldRole = user.getRole();
            UserRole newRole = UserRole.valueOf(newRoleStr.toUpperCase());
            user.setRole(newRole);

            log.info("사용자 권한 변경 - 대상: {} ({}) | 변경: {} -> {}",
                    user.getUserName(), userID, oldRole, newRole);

        } catch (IllegalArgumentException e) {
            log.warn("권한 변경 실패 (잘못된 Role): {} - 요청값: {}", userID, newRoleStr);
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

    // 6. 강제 탈퇴 로직 (DB에서 완전 삭제)
    @Transactional
    public void forceWithdrawal(String userId) {
        // 1. 유저 찾기
        User user = findUserByUserID(userId);
        String userName = user.getUserName(); // 삭제 후 로그에 남기기 위해 이름 미리 저장

        // 2. DB에서 완전히 삭제 (이래야 학번 중복 없이 재가입 가능)
        userLoginInfoRepository.delete(user);

        log.info("관리자 강제 탈퇴(DB 삭제) 처리 완료 - 대상: {} ({})", userName, userId);
    }

    private User findUserByUserID(String userID) {
        return userLoginInfoRepository.findByUserID(userID)
                .orElseThrow(() -> {
                    log.warn("관리자 작업 실패: 존재하지 않는 유저 ID ({})", userID);
                    return new UserNotFoundException("해당 유저를 찾을 수 없습니다: " + userID);
                });
    }
}