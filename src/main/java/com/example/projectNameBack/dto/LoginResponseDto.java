package com.example.projectNameBack.dto;

public class LoginResponseDto {
    private String token;       // JWT 토큰
    private UserInfoDto user;   // 유저 정보

    public LoginResponseDto(String token, UserInfoDto user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public UserInfoDto getUser() {
        return user;
    }
}
