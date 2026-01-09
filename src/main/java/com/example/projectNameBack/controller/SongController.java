package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.SongRegisterDto;
import com.example.projectNameBack.entity.SongRegister;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    // 1. 곡 등록
    @PostMapping
    public ResponseEntity<SongRegister> registerSong(@RequestBody SongRegisterDto dto) {
        SongRegister saved = songService.saveSongRegister(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 2. 이벤트 이름으로 곡 조회
    @GetMapping("/by-event")
    public ResponseEntity<List<SongRegisterDto>> getSongsByEvent(@RequestParam String eventName) {
        return ResponseEntity.ok(songService.getSongsByEvent(eventName));
    }

    // 4. 곡 정보 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateSong(@PathVariable Long id, @RequestBody SongRegisterDto dto) {
        songService.updateSong(id, dto);
        return ResponseEntity.ok("곡 정보가 수정되었습니다.");
    }

    // 5. 곡 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSong(@PathVariable Long id, @AuthenticationPrincipal Object principal) {
        String currentUserId = null;

        if (principal instanceof User) {
            currentUserId = ((User) principal).getUserID();
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            currentUserId = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else {
            currentUserId = principal.toString();
        }

        // 서비스 호출 (ID를 넘겨주면, 서비스 내부에서 이름으로 변환하여 검증함)
        songService.deleteSong(id, currentUserId);

        return ResponseEntity.ok("곡 등록이 취소되었습니다.");
    }
}