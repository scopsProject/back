package com.example.projectNameBack.repository;

import com.example.projectNameBack.dto.ReservationDto;
import com.example.projectNameBack.entity.Reservation;
import com.example.projectNameBack.entity.SongRegister;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.songRegister sr " +
            "LEFT JOIN FETCH sr.sessions " +
            "WHERE r.date BETWEEN :start AND :end")
    List<Reservation> findWithSessionsByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);
    List<Reservation> findByDateBetween(LocalDate start, LocalDate end);
    boolean existsByDateAndStartTimeLessThanAndEndTimeGreaterThan(
            LocalDate date,
            LocalTime endTime,
            LocalTime startTime
    );
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.date = :date " +
            "AND r.startTime < :endTime " +
            "AND r.endTime > :startTime")
    List<Reservation> findOverlappingReservations(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

}
