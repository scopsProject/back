package com.example.projectNameBack.exception;

import com.example.projectNameBack.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 비즈니스 로직 예외 (예: 이미 예약됨, 시간 아님 등) -> 409 Conflict
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.warn("IllegalStateException: {}", e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getMessage(), "CONFLICT");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 2. 잘못된 인자 (예: null 값, 잘못된 포맷) -> 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getMessage(), "BAD_REQUEST");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 3. 날짜 형식 오류 -> 400 Bad Request
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponse> handleDateTimeParseException(DateTimeParseException e) {
        log.warn("Date Format Error: {}", e.getMessage());
        ErrorResponse response = new ErrorResponse("날짜 형식이 올바르지 않습니다. (yyyy-MM-dd)", "INVALID_DATE_FORMAT");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 4. 리소스 찾을 수 없음 (User, Event, Song 등) -> 404 Not Found
    // (커스텀 예외들을 묶어서 처리)
    @ExceptionHandler({UserNotFoundException.class, EventNotFoundException.class, SongNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleResourceNotFound(RuntimeException e) {
        log.warn("Resource Not Found: {}", e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getMessage(), "NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 5. 인증 실패 -> 401 Unauthorized
    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorizedException(UnAuthorizedException e) {
        log.warn("Unauthorized: {}", e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getMessage(), "UNAUTHORIZED");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 6. 그 외 모든 서버 에러 -> 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected Error: ", e);
        ErrorResponse response = new ErrorResponse("서버 내부 오류가 발생했습니다: " + e.getMessage(), "INTERNAL_SERVER_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    // 중복 유저(학번) 예외 처리 -> 409 Conflict
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUserException(DuplicateUserException e) {
        log.warn("Duplicate User Error: {}", e.getMessage());
        // 프론트에서 구분하기 쉽게 에러 코드를 "DUPLICATE_USER"로 설정
        ErrorResponse response = new ErrorResponse(e.getMessage(), "DUPLICATE_USER");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}