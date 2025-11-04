package com.example.projectNameBack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserInfoDto {
    private String userName;
    private String session;
    private String role;
    private int userYear;

    public UserInfoDto(String userName, String session, int userYear) {
        this.userName = userName;
        this.session = session;
        this.userYear = userYear;
    }
}
