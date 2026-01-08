package com.example.projectNameBack.dto;

import com.example.projectNameBack.entity.Event;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class EventRequestDto {
    private String eventName;
    private LocalDate startDate; // 행사 시작일 (또는 생성일)
    private LocalDate endDate;     // 행사 종료일
    @JsonProperty("isSongRegistrationAvailable")
    private Boolean songRegistrationAvailable;

    public Event toEntity() {
        Event event = new Event();
        event.setEventName(this.eventName);
        event.setStartDate(this.startDate);
        event.setEndDate(this.endDate);
        event.setSongRegistrationAvailable(
                this.songRegistrationAvailable != null ? this.songRegistrationAvailable : false
        );
        return event;
    }
}