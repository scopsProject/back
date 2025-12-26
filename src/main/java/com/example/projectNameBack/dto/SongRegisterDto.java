package com.example.projectNameBack.dto;

import com.example.projectNameBack.entity.SongRegister;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class SongRegisterDto {
    private Long id;
    private String eventName;
    private String songName;
    private String singerName;
    private String userName;
    private LocalDate date;
    private List<SongSessionDto> sessions;

    public static SongRegisterDto from(SongRegister entity) {
        SongRegisterDto dto = new SongRegisterDto();
        dto.setId(entity.getId());
        dto.setEventName(entity.getEvent() != null ? entity.getEvent().getEventName() : null);
        dto.setSongName(entity.getSongName());
        dto.setSingerName(entity.getSingerName());
        dto.setUserName(entity.getUserName());
        dto.setSessions(entity.getSessions().stream()
                .map(SongSessionDto::from)
                .collect(Collectors.toList()));
        return dto;
    }
}