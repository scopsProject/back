package com.example.projectNameBack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek; // 🔥 자바 기본 요일 객체
import java.time.LocalTime; // 🔥 날짜 없는 '시간'만 저장

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;  // 수업명
    private String memo;   // 강의실 등 메모

    // 🔥 1. 요일 저장 (MONDAY, TUESDAY... 로 저장됨)
    // EnumType.STRING을 쓰면 DB에 숫자가 아니라 'MONDAY' 글자로 저장돼서 보기 편합니다.
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    // 🔥 2. 시간 저장 (날짜 없이 시:분:초 만 저장)
    private LocalTime startTime; // 예: 10:00:00
    private LocalTime endTime;   // 예: 12:00:00

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public void updateInfo(String title, String memo, DayOfWeek day, LocalTime start, LocalTime end) {
        this.title = title;
        this.memo = memo;
        this.dayOfWeek = day;
        this.startTime = start;
        this.endTime = end;
    }
}