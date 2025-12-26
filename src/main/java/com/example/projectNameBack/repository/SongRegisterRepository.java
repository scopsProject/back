package com.example.projectNameBack.repository;

import com.example.projectNameBack.entity.SongRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SongRegisterRepository extends JpaRepository <SongRegister, Long> {
    List<SongRegister> findByEvent_EventName(String eventName);
}
