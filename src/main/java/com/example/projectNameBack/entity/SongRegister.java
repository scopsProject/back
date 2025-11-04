package com.example.projectNameBack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SongRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String eventName;
    private String songName;
    private String singerName;
    private LocalDate date;

    @OneToMany(mappedBy = "songRegister", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SongSession> sessions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "song_register_users",
            joinColumns = @JoinColumn(name = "song_register_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();
}
