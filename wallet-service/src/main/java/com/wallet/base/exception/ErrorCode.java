package com.wallet.base.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "입력값이 올바르지 않습니다."),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "COMMON_002", "요청 본문이 올바르지 않습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH_001", "이미 가입된 이메일입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_002", "인증이 필요합니다."),
    UNKNOW_USER(HttpStatus.UNAUTHORIZED, "AUTH_003", "이메일 혹은 비밀번호가 올바르지 않습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_004", "접근 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_005", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_006", "만료된 토큰입니다."),
    USER_INACTIVE(HttpStatus.UNAUTHORIZED, "AUTH_007", "사용자가 비활성화되었습니다."),
    UNMATCHED_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH_008", "비밀번호가 일치하지 않습니다."),
    WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "WALLET_001", "지갑을 찾을 수 없습니다."),
    WALLET_HAS_BALANCE(HttpStatus.BAD_REQUEST, "WALLET_002", "지갑에 잔액이 있습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_999", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
