package com.example.projectNameBack.dto;

import com.example.projectNameBack.entity.User; // 🔥 [핵심] 이 줄이 빠져서 에러가 났을 겁니다!
import com.example.projectNameBack.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserInfoDto {
    private String userID;
    private String userName;
    private String session;
    private UserRole role;
    private int userYear;
    private String status;

    public static UserInfoDto from(User user) {
        return new UserInfoDto(
                user.getUserID(),
                user.getUserName(),
                user.getSession(),
                user.getRole(),
                user.getUserYear(),
                user.getStatus().name()
        );
    }
}