package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.LoginResponseDto;
import com.example.projectNameBack.dto.SaveUserLoginInfoDto;
import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.entity.UserStatus;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import com.example.projectNameBack.util.JwtUtil; // ⬅️ 이 import는 이미 있습니다.
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. ⬇️ JwtUtil 필드를 추가합니다.
    private final JwtUtil jwtUtil;

    // 2. ⬇️ 생성자에 JwtUtil을 추가하여 Spring으로부터 주입받습니다.
    public AuthService(UserLoginInfoRepository userLoginInfoRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil){ // ⬅️ 파라미터 추가
        this.userLoginInfoRepository = userLoginInfoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil; // ⬅️ 필드 초기화
    }

    public LoginResponseDto login(String userID, String rawPassword){
        Optional<User> userOpt = userLoginInfoRepository.findByUserID(userID);

        // 1. 유저가 존재하는지 먼저 확인
        if (userOpt.isPresent()) {
            User user = userOpt.get(); // 껍데기 벗기기 (User 객체 꺼냄)

            // 2. 비밀번호 확인
            if (passwordEncoder.matches(rawPassword, user.getUserPassword())) {

                // 3. ★ 여기서 상태 확인 (비밀번호가 맞은 사람만 상태 체크) ★
                if (user.getStatus() == UserStatus.PENDING) {
                    throw new RuntimeException("관리자 승인 대기 중인 계정입니다.");
                }
                if (user.getStatus() == UserStatus.REJECTED) {
                    throw new RuntimeException("가입이 거절된 계정입니다.");
                }

                // 4. 통과되면 토큰 생성
                String token = jwtUtil.generateToken(
                        user.getUserID(),
                        user.getUserName(),
                        user.getRole()
                );

                UserInfoDto userInfo = new UserInfoDto(
                        user.getUserID(),
                        user.getUserName(),
                        user.getSession(),
                        user.getRole(),
                        user.getUserYear(),
                        user.getStatus().name()
                );

                System.out.println("로그인 성공: " + user.getUserName());
                return new LoginResponseDto(token, userInfo);
            }
        }

        // 아이디가 없거나 비밀번호가 틀림
        return null;
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
        Optional<User> userOpt = userLoginInfoRepository.findByUserID(userID);
        if (userOpt.isPresent()) {
            userLoginInfoRepository.delete(userOpt.get());
        }
    }
}