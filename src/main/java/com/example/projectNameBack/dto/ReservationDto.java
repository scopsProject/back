package com.example.projectNameBack.dto;

import com.example.projectNameBack.entity.Reservation;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long id;
    private String eventName;
    private String songName;
    private String singerName;
    private List<SongSessionDto> sessions;

    public static ReservationDto fromEntity(Reservation entity) {
        return ReservationDto.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .songName(entity.getSongName())
                .singerName(entity.getSingerName())
                .build();
    }


}

