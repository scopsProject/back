package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.TimeTableDto;
import com.example.projectNameBack.entity.TimeTable;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.repository.TimeTableRepository;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TimeTableService {
    private final TimeTableRepository timeTableRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;

    @Transactional
    public TimeTable addTimeTable(String userId, TimeTableDto dto) {
        // 🔥 [수정] findByUserName -> findByUserID
        // (토큰에 들어있는 건 '이름'이 아니라 '학번(ID)'이기 때문입니다)
        User user = userLoginInfoRepository.findByUserID(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        boolean isOverlapped = timeTableRepository.existsOverlap(
                user.getId(),
                dto.getDayOfWeek(),
                dto.getStartTime(),
                dto.getEndTime()
        );
        if (isOverlapped) {
            throw new IllegalArgumentException("해당 시간에 이미 일정이 존재합니다.");
        }
        TimeTable timeTable = new TimeTable();
        timeTable.setTitle(dto.getTitle());
        timeTable.setMemo(dto.getMemo());
        timeTable.setDayOfWeek(dto.getDayOfWeek());
        timeTable.setStartTime(dto.getStartTime());
        timeTable.setEndTime(dto.getEndTime());
        timeTable.setUser(user);

        return timeTableRepository.save(timeTable);
    }
    // TimeTableService.java

    @Transactional(readOnly = true)
    public List<TimeTableDto> getTimeTables(String userId) {
        User user = userLoginInfoRepository.findByUserID(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // Entity 리스트 -> DTO 리스트 변환
        return user.getTimeTables().stream()
                .map(t -> new TimeTableDto(
                        t.getId(),
                        t.getTitle(),
                        t.getMemo(),
                        t.getStartTime(),
                        t.getEndTime(),
                        t.getDayOfWeek()
                ))
                .collect(Collectors.toList());
    }
    // ...
    @Transactional
    public void updateTimeTable(Long id, String userId, TimeTableDto dto) {
        TimeTable timeTable = timeTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));

        // 본인 확인 (선택사항이지만 보안상 좋음)
        if (!timeTable.getUser().getUserID().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 🔥 중복 검사 (내 ID 제외)
        boolean isOverlapped = timeTableRepository.existsOverlapForUpdate(
                timeTable.getUser().getId(),
                id, // 내 ID
                dto.getDayOfWeek(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (isOverlapped) {
            throw new IllegalArgumentException("해당 시간에 이미 일정이 존재합니다.");
        }

        // 업데이트
        timeTable.setTitle(dto.getTitle());
        timeTable.setMemo(dto.getMemo());
        timeTable.setDayOfWeek(dto.getDayOfWeek());
        timeTable.setStartTime(dto.getStartTime());
        timeTable.setEndTime(dto.getEndTime());
        // save 안 해도 Transactional 덕분에 자동 업데이트 (Dirty Checking)
    }

    @Transactional
    public void deleteTimeTable(Long id, String userId) {
        TimeTable timeTable = timeTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));

        // 본인 확인
        if (!timeTable.getUser().getUserID().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        timeTableRepository.delete(timeTable);
    }
// ...
}
