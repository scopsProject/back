package com.example.projectNameBack.repository;

import com.example.projectNameBack.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    @Query("SELECT COUNT(t) > 0 " +
            "FROM TimeTable t " +
            "WHERE t.user.id = :userId " +
            "AND t.dayOfWeek = :dayOfWeek " +
            "AND t.startTime < :endTime " +
            "AND t.endTime > :startTime")
    boolean existsOverlap(@Param("userId") Long userId,
                          @Param("dayOfWeek") DayOfWeek dayOfWeek,
                          @Param("startTime") LocalTime startTime,
                          @Param("endTime") LocalTime endTime);
}
