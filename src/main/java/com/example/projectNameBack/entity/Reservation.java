package com.example.projectNameBack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @JoinColumn(name = "event_id", nullable = true)
    private Event event;
    private String singerName;
    private String songName;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 예약 사용자
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // SongRegister (필수 아니면 optional)
    @ManyToOne
    @JoinColumn(name = "song_register_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SongRegister songRegister;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationType type;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

