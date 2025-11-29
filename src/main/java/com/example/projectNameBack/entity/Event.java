package com.example.projectNameBack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventName;
    private LocalDate createdDate;
    private LocalDate endDate;
    // mappedBy = "event": SongRegister의 'event' 필드가 주인이라는 뜻
    // cascade = CascadeType.ALL: Event를 저장/삭제할 때 자식도 똑같이 처리함 (삭제 시 같이 삭제됨)
    // orphanRemoval = true: 리스트에서 제거되면 DB에서도 삭제됨
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SongRegister> songRegisters = new ArrayList<>();
}
