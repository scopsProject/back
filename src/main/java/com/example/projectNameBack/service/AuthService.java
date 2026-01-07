package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.LoginResponseDto;
import com.example.projectNameBack.dto.SaveUserLoginInfoDto;
import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.entity.UserStatus;
import com.example.projectNameBack.exception.DuplicateUserException;
import com.example.projectNameBack.exception.UnAuthorizedException;
import com.example.projectNameBack.exception.UserNotFoundException;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import com.example.projectNameBack.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public LoginResponseDto login(String userID, String rawPassword) {
        // 1. 유저 조회
        User user = findUserByUserID(userID);

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(rawPassword, user.getUserPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 상태 검증 (탈퇴 회원 차단 포함)
        validateUserStatus(user);

        // 4. 토큰 생성 및 응답
        String token = jwtUtil.generateToken(
                user.getUserID(),
                user.getUserName(),
                user.getRole()
        );

        UserInfoDto userInfo = UserInfoDto.from(user);
        log.info("로그인 성공: {}", user.getUserName());
        return new LoginResponseDto(token, userInfo);
    }

    @Transactional
    public User saveUserInfo(SaveUserLoginInfoDto dto) {
        if (userLoginInfoRepository.findByUserID(dto.getUserID()).isPresent()) {
            throw new DuplicateUserException("이미 가입된 학번입니다.");
        }

        User user = new User();
        user.setUserID(dto.getUserID());
        user.setUserPassword(passwordEncoder.encode(dto.getUserPassword()));
        user.setUserName(dto.getUserName());
        user.setUserYear(dto.getUserYear());
        user.setSession(dto.getSession());
        user.setRole(dto.getRole());
        user.setStatus(UserStatus.PENDING);

        return userLoginInfoRepository.save(user);
    }

    // 회원 탈퇴 (본인 요청)
    @Transactional
    public void deleteUserCompletely(UserDetails userDetails) {
        // 1. 인증 정보 검증
        if (userDetails == null) {
            throw new UnAuthorizedException("인증 정보가 유효하지 않습니다.");
        }

        // 2. 유저 조회
        String userID = userDetails.getUsername();
        User user = findUserByUserID(userID);

        // 3. 삭제(delete) 대신 상태 변경(WITHDRAWN)
        user.setStatus(UserStatus.WITHDRAWN);

    }

    @Transactional(readOnly = true)
    public List<UserInfoDto> getSessions(String myId) {
        // ACTIVE만 조회하므로 탈퇴 회원은 자동으로 목록에서 빠짐
        return userLoginInfoRepository.findByUserIDNotAndStatus(myId, UserStatus.ACTIVE).stream()
                .map(UserInfoDto::from)
                .collect(Collectors.toList());
    }

    private User findUserByUserID(String userID) {
        return userLoginInfoRepository.findByUserID(userID)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다: " + userID));
    }

    private void validateUserStatus(User user) {
        if (user.getStatus() == UserStatus.PENDING) {
            throw new IllegalStateException("관리자 승인 대기 중인 계정입니다.");
        }
        if (user.getStatus() == UserStatus.REJECTED) {
            throw new IllegalStateException("가입이 거절된 계정입니다.");
        }
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new IllegalStateException("탈퇴한 계정입니다.");
        }
    }
}