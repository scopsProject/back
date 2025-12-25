package com.example.projectNameBack.dto;

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
}
