package com.example.projectNameBack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userID; // 로그인 ID
    private String userPassword;
    private String userName;
    private String session;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private int userYear;
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // 1. 예약 (Reservation): orphanRemoval = true 추가함
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();

    // 2. 시간표 (TimeTable): 기존에 잘 작성됨
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TimeTable> timeTables = new ArrayList<>();

    // 3. 합주 세션 (SongSession): 아까 에러났던 부분이라 반드시 추가해야 함!
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SongSession> songSessions = new ArrayList<>();

    public User(String userName, int userYear, String session, String userID, String userPassword, UserRole role) {
        this.userName = userName;
        this.userYear = userYear;
        this.session = session;
        this.userID = userID;
        this.userPassword = userPassword;
        this.role = role;
    }
}