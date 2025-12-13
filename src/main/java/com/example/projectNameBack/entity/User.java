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
    private String role;
    private int userYear;
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // 🔥 유저는 여러 예약을 가질 수 있음 (1:N)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TimeTable> timeTables = new ArrayList<>();

    public User(String userName, int userYear, String session, String userID, String userPassword, String role) {
        this.userName = userName;
        this.userYear = userYear;
        this.session = session;
        this.userID = userID;
        this.userPassword = userPassword;
        this.role = role;
    }
}
