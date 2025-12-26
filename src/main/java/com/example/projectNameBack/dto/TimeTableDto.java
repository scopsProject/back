package com.example.projectNameBack.dto;

import com.example.projectNameBack.entity.TimeTable;
import com.example.projectNameBack.entity.User;
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

    public static TimeTableDto from(TimeTable entity) {
        return new TimeTableDto(
                entity.getId(),
                entity.getTitle(),
                entity.getMemo(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getDayOfWeek()
        );
    }

    public TimeTable toEntity(User user) {
        TimeTable timeTable = new TimeTable();
        timeTable.setUser(user); // 유저 연결
        timeTable.setTitle(this.title);
        timeTable.setMemo(this.memo);
        timeTable.setDayOfWeek(this.dayOfWeek);
        timeTable.setStartTime(this.startTime);
        timeTable.setEndTime(this.endTime);
        return timeTable;
    }
}
