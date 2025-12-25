package com.example.projectNameBack.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class RoleUpdateRequest {
    private String role; // "ADMIN", "USER" 등
}