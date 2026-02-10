package com.ansim.map.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 (COMMON)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "허용되지 않은 메소드입니다."),
    ACCESS_DENIED(HttpStatus.BAD_REQUEST,"C004","해당 요청에 대한 권한이 없습니다."),

    // 인증 (AUTH)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A002", "아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A004", "토큰이 만료되었습니다."),

    // 회원 (MEMBER)
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "존재하지 않는 회원입니다."),
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "M002", "이미 존재하는 이메일입니다."),

    // 즐겨찾기 (FAVORITE)
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND,"F001","존재하지 않는 즐겨찾기입니다."),
    FAVORITE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST,"FOO2" ,"즐겨찾기는 최대 5개까지만 등록 가능합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}