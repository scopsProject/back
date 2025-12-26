package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.TimeTableDto;
import com.example.projectNameBack.entity.TimeTable;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.repository.TimeTableRepository;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@RequiredArgsConstructor
@Service
public class TimeTableService {
    private final TimeTableRepository timeTableRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;

    @Transactional
    public TimeTable addTimeTable(String userId, TimeTableDto dto) {
        User user = findUserByUserID(userId);

        validateOverlap(user.getId(), dto);

        TimeTable timeTable = dto.toEntity(user);

        log.info("시간표 추가: {} ({})", dto.getTitle(), userId);
        return timeTableRepository.save(timeTable);
    }

    @Transactional(readOnly = true)
    public List<TimeTableDto> getTimeTables(String userId) {
        User user = findUserByUserID(userId);

        return user.getTimeTables().stream()
                .map(TimeTableDto::from)
                .collect(Collectors.toList());
    }
    @Transactional
    public void updateTimeTable(Long id, String userId, TimeTableDto dto) {
        TimeTable timeTable = findTimeTableWithOwnership(id, userId);

        boolean isOverlapped = timeTableRepository.existsOverlapForUpdate(
                timeTable.getUser().getId(),
                id,
                dto.getDayOfWeek(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (isOverlapped) {
            throw new IllegalArgumentException("수정하려는 시간에 이미 일정이 존재합니다.");
        }

        timeTable.updateInfo(
                dto.getTitle(), dto.getMemo(), dto.getDayOfWeek(),
                dto.getStartTime(), dto.getEndTime()
        );

        log.info("시간표 수정 완료: ID={}", id);
    }

    @Transactional
    public void deleteTimeTable(Long id, String userId) {
        TimeTable timeTable = findTimeTableWithOwnership(id, userId);

        timeTableRepository.delete(timeTable);
        log.info("시간표 삭제 완료: ID={}", id);
    }

    private User findUserByUserID(String userId) {
        return userLoginInfoRepository.findByUserID(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    // 시간표 조회 + 본인 확인 동시에 처리
    private TimeTable findTimeTableWithOwnership(Long id, String userId) {
        TimeTable timeTable = timeTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));

        if (!timeTable.getUser().getUserID().equals(userId)) {
            log.warn("권한 없는 수정 시도: User={}, TimeTableId={}", userId, id);
            throw new IllegalArgumentException("본인의 시간표만 수정/삭제할 수 있습니다.");
        }
        return timeTable;
    }

    private void validateOverlap(Long dbUserId, TimeTableDto dto) {
        boolean isOverlapped = timeTableRepository.existsOverlap(
                dbUserId,
                dto.getDayOfWeek(),
                dto.getStartTime(),
                dto.getEndTime()
        );
        if (isOverlapped) {
            throw new IllegalArgumentException("해당 시간에 이미 일정이 존재합니다.");
        }
    }
}
