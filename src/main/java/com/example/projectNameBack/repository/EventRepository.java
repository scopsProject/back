package com.example.projectNameBack.repository;

import com.example.projectNameBack.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByEventName(String eventName);

    @Query("SELECT e FROM Event e WHERE e.endDate >= :start AND e.startDate <= :end")
    List<Event> findEventsByPeriod(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT e.eventName FROM Event e WHERE e.isSongRegistrationAvailable = true")
    List<String> findAvailableEventNames();
}
