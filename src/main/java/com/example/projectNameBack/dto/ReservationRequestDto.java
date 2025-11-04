package com.example.projectNameBack.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ReservationRequestDto {
    private String singerName;  // 프론트에 맞춤
    private String songName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private Long songRegisterId;
    private String eventName;
    private List<SongSessionDto> sessions;

}
