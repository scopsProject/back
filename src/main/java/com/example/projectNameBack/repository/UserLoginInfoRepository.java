package com.example.projectNameBack.repository;

import com.example.projectNameBack.dto.UserInfoDto;
import com.example.projectNameBack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLoginInfoRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserID(String userID);
    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.userID = :userID")
    int deleteByUserID(@Param("userID") String userID);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM song_register_users WHERE user_id = :userID", nativeQuery = true)
    int deleteUserFromReservations(@Param("userID") Long userId);
    @Query("SELECT u FROM User u")
    List<User> findAllUsers();
}