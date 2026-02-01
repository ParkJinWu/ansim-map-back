package com.ansim.map.global.exception;

import lombok.Getter;

@Getter
public class AnsimException extends RuntimeException {
    private final ErrorCode errorCode;

    public AnsimException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AnsimException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}