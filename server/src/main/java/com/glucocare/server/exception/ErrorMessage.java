package com.glucocare.server.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    UNAUTHORIZED_ACCESS(401, "허용되지 않는 접근입니다."),
    NOT_FOUND(404, "찾을 수 없습니다."),
    INVALID_LOGIN_REQUEST_MATCHES(401, "해당 이메일과 패스워드에 해당하는 이용자 정보가 존재하지 않습니다."),
    DUPLICATED_EMAIL(409, "이미 존재하는 이메일입니다."),
    BAD_REQUEST(400, "잘못된 요청입니다.");

    private final int code;
    private final String message;

    ErrorMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
