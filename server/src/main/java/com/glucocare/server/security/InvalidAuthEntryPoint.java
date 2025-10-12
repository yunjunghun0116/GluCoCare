package com.glucocare.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glucocare.server.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static java.nio.charset.StandardCharsets.UTF_8;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증 실패 엔트리 포인트
 * <p>
 * Spring Security에서 비인증 사용자가 보호된 리소스에 접근할 때 호출되는 엔트리 포인트입니다.
 * 401 Unauthorized 상태코드와 에러 메시지를 JSON 형태로 반환합니다.
 */
@Component
@RequiredArgsConstructor
public class InvalidAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * 인증 실패 시 401 Unauthorized 응답 반환
     * <p>
     * 처리 단계:
     * 1. 요청 속성에서 예외 메시지 추출
     * 2. 예외 응답 객체 생성 (상태코드 401, 에러 메시지 포함)
     * 3. 응답 헤더 설정 (Content-Type: application/json, Character-Encoding: UTF-8)
     * 4. 응답 상태코드 401 설정
     * 5. JSON 형태로 예외 응답 작성
     *
     * @param request       HTTP 요청 객체
     * @param response      HTTP 응답 객체
     * @param authException 인증 예외
     * @throws IOException IO 예외
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        var exceptionMessage = (String) request.getAttribute("exception");
        var exceptionResponse = new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(), exceptionMessage);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        objectMapper.writeValue(response.getWriter(), exceptionResponse);
    }
}
