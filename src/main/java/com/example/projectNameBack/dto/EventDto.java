package com.example.projectNameBack.dto;

import com.example.projectNameBack.entity.Event;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;
    private String eventName;
    private LocalDate startDate;
    private LocalDate endDate;
    @JsonProperty("isSongRegistrationAvailable")
    private boolean isSongRegistrationAvailable;

    public static EventDto fromEntity(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .eventName(event.getEventName())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .isSongRegistrationAvailable(event.isSongRegistrationAvailable())
                .build();
    }
}