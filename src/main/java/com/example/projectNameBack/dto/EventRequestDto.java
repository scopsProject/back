package com.example.projectNameBack.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class EventRequestDto {
    private String eventName;
    private LocalDate createdDate; // 행사 시작일 (또는 생성일)
    private LocalDate endDate;     // 행사 종료일
    private boolean isSongRegistrationAvailable;
}