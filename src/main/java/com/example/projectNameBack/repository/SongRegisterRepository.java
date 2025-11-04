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
    @Query("SELECT s FROM SongRegister s WHERE s.eventName = :eventName")
    List<SongRegister> findByEventName(@Param("eventName") String eventName);

    @Query("SELECT s FROM SongRegister s WHERE s.songName = :songName")
    List<SongRegister> findBySongName(@Param("songName") String songName);

    @Query("SELECT DISTINCT s.eventName FROM SongRegister s")
    List<String> findDistinctEventNames();

}
