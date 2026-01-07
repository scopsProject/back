package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.TimeTableDto;
import com.example.projectNameBack.entity.TimeTable;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.exception.UserNotFoundException;
import com.example.projectNameBack.repository.TimeTableRepository;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
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

        // 시간 선후 관계 검증
        validateTimeOrder(dto);

        // 시간표 겹침 검증
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

        // 시간 선후 관계 검증
        validateTimeOrder(dto);

        // 수정 시 겹침 확인 (자기 자신 제외)
        boolean isOverlapped = timeTableRepository.existsOverlapForUpdate(
                timeTable.getUser().getId(),
                id,
                dto.getDayOfWeek(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (isOverlapped) {
            // [요청 사항] 겹침 시 메시지와 함께 예외 발생
            throw new IllegalStateException("이미 해당 시간대에 시간표가 존재합니다.");
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

    // --- 헬퍼 메서드 ---

    private User findUserByUserID(String userId) {
        return userLoginInfoRepository.findByUserID(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    // 시간표 조회 + 본인 확인
    private TimeTable findTimeTableWithOwnership(Long id, String userId) {
        TimeTable timeTable = timeTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 시간표를 찾을 수 없습니다."));

        if (!timeTable.getUser().getUserID().equals(userId)) {
            // 403 Forbidden 처리를 위해 AccessDeniedException 사용
            throw new AccessDeniedException("본인의 시간표만 수정/삭제할 수 있습니다.");
        }
        return timeTable;
    }

    // 겹침 검증 로직
    private void validateOverlap(Long dbUserId, TimeTableDto dto) {
        boolean isOverlapped = timeTableRepository.existsOverlap(
                dbUserId,
                dto.getDayOfWeek(),
                dto.getStartTime(),
                dto.getEndTime()
        );
        if (isOverlapped) {
            // [요청 사항] 겹침 에러 메시지
            throw new IllegalStateException("이미 해당 시간대에 시간표가 존재합니다.");
        }
    }
    // 시간 검증 헬퍼 메서드
    private void validateTimeOrder(TimeTableDto dto) {
        // LocalTime은 compareTo나 isAfter 등으로 비교 가능
        // dto.getStartTime()이 09:00:00 형태의 LocalTime이라고 가정
        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 합니다.");
        }
    }
}