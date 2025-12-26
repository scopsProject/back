package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.LoginResponseDto;
import com.example.projectNameBack.dto.SaveUserLoginInfoDto;
import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.entity.UserStatus;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import com.example.projectNameBack.util.JwtUtil; // ⬅️ 이 import는 이미 있습니다.
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true) // 읽기 전용 트랜잭션 (성능 최적화)
    public LoginResponseDto login(String userID, String rawPassword) {
        // 1. 유저 조회 (없으면 바로 예외 발생)
        User user = userLoginInfoRepository.findByUserID(userID)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 비밀번호 검증 (틀리면 바로 튕겨냄 - Guard Clause)
        if (!passwordEncoder.matches(rawPassword, user.getUserPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 상태 검증 (ACTIVE가 아니면 에러)
        validateUserStatus(user);

        // 4. 토큰 생성
        String token = jwtUtil.generateToken(
                user.getUserID(),
                user.getUserName(),
                user.getRole()
        );

        UserInfoDto userInfo = UserInfoDto.from(user);

        log.info("로그인 성공: {}", user.getUserName());
        return new LoginResponseDto(token, userInfo);
    }


    public User saveUserInfo(SaveUserLoginInfoDto saveUserLoginInfoDto){
        if (userLoginInfoRepository.findByUserID(saveUserLoginInfoDto.getUserID()).isPresent()) {
            throw new IllegalStateException("이미 가입된 학번입니다.");
        }
        User user = new User();
        user.setUserID(saveUserLoginInfoDto.getUserID());
        user.setUserPassword(passwordEncoder.encode(saveUserLoginInfoDto.getUserPassword()));
        user.setUserName(saveUserLoginInfoDto.getUserName());
        user.setUserYear(saveUserLoginInfoDto.getUserYear());
        user.setSession(saveUserLoginInfoDto.getSession());
        user.setRole(saveUserLoginInfoDto.getRole());
        user.setStatus(UserStatus.PENDING);
        return userLoginInfoRepository.save(user);
    }

    @Transactional
    public void deleteUserCompletely(String userID) {
        User user = userLoginInfoRepository.findByUserID(userID)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 유저를 찾을 수 없습니다."));

        userLoginInfoRepository.delete(user);
    }
    private void validateUserStatus(User user) {
        if (user.getStatus() == UserStatus.PENDING) {
            throw new IllegalStateException("관리자 승인 대기 중인 계정입니다.");
        }
        if (user.getStatus() == UserStatus.REJECTED) {
            throw new IllegalStateException("가입이 거절된 계정입니다.");
        }
    }
    // AuthService.java 또는 UserService.java 로 이동
    public List<UserInfoDto> getSessions(String myId) {
        return userLoginInfoRepository.findByUserIDNotAndStatus(myId, UserStatus.ACTIVE).stream()
                .map(UserInfoDto::from)
                .collect(Collectors.toList());
    }
}