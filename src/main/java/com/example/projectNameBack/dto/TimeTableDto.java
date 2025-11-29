package com.example.projectNameBack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeTableDto {
    private Long id;
    private String title;
    private String memo;
    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek dayOfWeek;
}
