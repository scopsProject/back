package com.example.projectNameBack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
    private String singerName;
    private String songName;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    // 예약 사용자
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // SongRegister (필수 아니면 optional)
    @ManyToOne
    @JoinColumn(name = "song_register_id")
    private SongRegister songRegister;
}

