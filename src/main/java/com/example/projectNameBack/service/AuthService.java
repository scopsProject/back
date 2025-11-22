package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.LoginResponseDto;
import com.example.projectNameBack.dto.SaveUserLoginInfoDto;
import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.User;
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

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("DB PW: " + user.getUserPassword());
            System.out.println("입력 PW: " + rawPassword);

            if (passwordEncoder.matches(rawPassword, user.getUserPassword())) {

                // JWT 생성 (userId, userName, role 포함)
                String token = jwtUtil.generateToken(
                        user.getUserID(),
                        user.getUserName(),
                        user.getRole()
                );

                // 프론트에 내려줄 사용자 정보 DTO
                UserInfoDto userInfo = new UserInfoDto(
                        user.getUserName(),
                        user.getSession(),
                        user.getUserYear(),
                        user.getRole()
                );

                System.out.println("로그인 성공: " + user.getUserName());
                return new LoginResponseDto(token, userInfo);
            }
        }

        return null;
    }


    public User saveUserInfo(SaveUserLoginInfoDto saveUserLoginInfoDto){
        User user = new User();
        user.setUserID(saveUserLoginInfoDto.getUserID());
        user.setUserPassword(passwordEncoder.encode(saveUserLoginInfoDto.getUserPassword()));
        user.setUserName(saveUserLoginInfoDto.getUserName());
        user.setUserYear(saveUserLoginInfoDto.getUserYear());
        user.setSession(saveUserLoginInfoDto.getSession());
        user.setRole(saveUserLoginInfoDto.getRole()); // ⬅️ (참고) 권한(role)을 "USER" 등으로 설정하는 것을 권장합니다.
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