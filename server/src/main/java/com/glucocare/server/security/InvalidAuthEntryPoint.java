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
 * 인증 실패 시 예외 처리 핸들러
 * Spring Security에서 비인증 사용자가 보호된 리소스에 접근할 때 호출되는 엔트리 포인트
 */
@Component
@RequiredArgsConstructor
public class InvalidAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * 인증 실패 시 호출되는 메서드
     * 401 Unauthorized 상태코드와 에러 메시지를 JSON 형태로 반환
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
