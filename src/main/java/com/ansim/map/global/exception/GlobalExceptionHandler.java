package com.ansim.map.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 우리가 만든 AnsimException 처리
    @ExceptionHandler(AnsimException.class)
    protected ResponseEntity<ErrorResponse> handleAnsimException(AnsimException e) {
        log.warn("AnsimException occurred: {}", e.getErrorCode().getCode());
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(e.getMessage()) // 생성자에서 넘긴 커스텀 메시지가 있으면 그걸 쓰고, 없으면 기본 메시지
                .build();

        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // 그 외 예상치 못한 모든 예외 처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected Exception: ", e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse response = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return new ResponseEntity<>(response, errorCode.getStatus());
    }
}