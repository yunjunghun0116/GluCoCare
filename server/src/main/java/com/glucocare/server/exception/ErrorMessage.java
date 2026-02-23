package com.glucocare.server.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    EXPIRED_TOKEN(401, "만료된 토큰입니다."),
    UNAUTHORIZED_ACCESS(401, "허용되지 않는 접근입니다."),
    NOT_FOUND(404, "찾을 수 없습니다."),
    INVALID_ACCESS(401, "사용자에게 허가되지 않는 접근입니다."),
    INVALID_LOGIN_REQUEST_MATCHES(401, "해당 이메일과 패스워드에 해당하는 이용자 정보가 존재하지 않습니다."),
    DUPLICATED_EMAIL(409, "이미 존재하는 이메일입니다."),
    INVALID_CONVERT_REQUEST(400, "잘못된 타입으로의 변환 요청입니다."),
    ALREADY_EXISTS(409, "이미 존재하는 객체입니다."),
    ALREADY_EXISTS_PATIENT(409, "환자 등록은 최대 1명까지만 할 수 있습니다."),
    BAD_REQUEST(400, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부의 문제로 인해 요청을 처리할 수 없습니다."),
    NEED_MORE_GLUCOSE_HISTORIES(400, "예측에 필요한 혈당 개수가 부족합니다. 잠시 후에 다시 수행해 주세요."),
    GENERATE_ACCESS_CODE_ERROR(400, "고유 코드를 만드는 과정에서 에러가 발생했습니다. 잠시 후 다시 시도해 주세요.");

    private final int code;
    private final String message;

    ErrorMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
