package com.example.projectNameBack.dto;
//asdasdasd
import com.example.projectNameBack.entity.Reservation;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private String userName;

    public static ReservationDto fromEntity(Reservation entity) {
        return ReservationDto.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .songName(entity.getSongName())
                .singerName(entity.getSingerName())
                .eventName(entity.getEvent() != null ? entity.getEvent().getEventName() : null)
                .userName(entity.getUser().getUserName())
                .sessions(entity.getSongRegister() != null ?
                        entity.getSongRegister().getSessions().stream()
                                .map(session -> new SongSessionDto(
                                        session.getSessionType(),
                                        // 유저 이름 꺼내기 (Null 체크 포함)
                                        session.getPlayer() != null ? session.getPlayer().getUserName() : "알 수 없음"
                                ))
                                .collect(Collectors.toList())
                        : List.of())
                .build();
    }
}

