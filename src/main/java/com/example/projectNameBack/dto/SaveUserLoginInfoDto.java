package com.example.projectNameBack.dto;

import com.example.projectNameBack.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SaveUserLoginInfoDto {
    private String userName;
    private int userYear;
    private String session;
    private String userID;
    private String userPassword;
    private String role;

    public User toEntity(){
        return new User(userName, userYear, session, userID, userPassword, role);
    }
}
