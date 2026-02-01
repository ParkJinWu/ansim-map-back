package com.ansim.map.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    private final String code;
    private final String message;

    // 유효성 검사 실패 시 어떤 필드가 틀렸는지 담는 용도 (없으면 JSON에 포함 안 함)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Map<String, String> errors;
}