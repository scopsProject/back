package com.example.projectNameBack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventName;
    private LocalDate startDate;
    private LocalDate endDate;

    @Column(columnDefinition = "boolean default false")
    private boolean isSongRegistrationAvailable;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SongRegister> songRegisters = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();

    public void updateEvent(String eventName, LocalDate startDate, LocalDate endDate) {
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
        // isSongRegistrationAvailable 수정도 여기서 처리
    }
}
