package com.example.projectNameBack.service;

import com.example.projectNameBack.dto.TimeTableDto;
import com.example.projectNameBack.entity.TimeTable;
import com.example.projectNameBack.entity.User;
import com.example.projectNameBack.repository.TimeTableRepository;
import com.example.projectNameBack.repository.UserLoginInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TimeTableService {
    private final TimeTableRepository timeTableRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;

    @Transactional
    public TimeTable addTimeTable(String userName, TimeTableDto dto) {
        User user = userLoginInfoRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        TimeTable timeTable = new TimeTable();
        timeTable.setTitle(dto.getTitle());
        timeTable.setMemo(dto.getMemo());
        timeTable.setDayOfWeek(dto.getDayOfWeek());
        timeTable.setStartTime(dto.getStartTime());
        timeTable.setEndTime(dto.getEndTime());
        timeTable.setUser(user);

        return timeTableRepository.save(timeTable);
    }
}
