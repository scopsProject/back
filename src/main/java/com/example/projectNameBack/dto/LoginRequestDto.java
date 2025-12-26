package com.example.projectNameBack.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class LoginRequestDto {
    private String userID;
    private String password;
}