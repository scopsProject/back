package com.example.projectNameBack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SongSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionType;   // 예: V, G, D

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_register_id")
    @JsonIgnore
    private SongRegister songRegister;
}
