package com.example.projectNameBack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToMany(mappedBy = "participants")
    private Set<SongRegister> reservations = new HashSet<>();

    public User(String userName, int userYear, String session, String userID, String userPassword, String role) {
        this.userName = userName;
        this.userYear = userYear;
        this.session = session;
        this.userID = userID;
        this.userPassword = userPassword;
        this.role = role;
    }
}
