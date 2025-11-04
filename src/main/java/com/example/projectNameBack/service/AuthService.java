package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.LoginResponseDto;
import com.example.projectNameBack.dto.SaveUserLoginInfoDto;
import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import com.example.projectNameBack.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final PasswordEncoder passwordEncoder;
    public AuthService(UserLoginInfoRepository userLoginInfoRepository, PasswordEncoder passwordEncoder){
        this.userLoginInfoRepository = userLoginInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDto login(String userID, String rawPassword){
        Optional<User> userOpt = userLoginInfoRepository.findByUserID(userID);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            System.out.println("DB PW: " + user.getUserPassword());
            System.out.println("입력 PW: " + rawPassword);
            if(passwordEncoder.matches(rawPassword, user.getUserPassword())){
                String token = JwtUtil.generateToken(user.getUserID());
                UserInfoDto userInfo = new UserInfoDto(
                        user.getUserName(),
                        user.getSession(),
                        user.getUserYear()
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
        user.setRole("none");
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
