package com.example.projectNameBack.controller;

import com.example.projectNameBack.dto.SongRegisterDto;
import com.example.projectNameBack.entity.SongRegister;
import com.example.projectNameBack.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.ok("곡이 삭제되었습니다.");
    }
}