package com.glucocare.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.exception.ExceptionResponse;
import com.glucocare.server.feature.member.domain.MemberRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT 인증 필터
 * 모든 HTTP 요청에 대해 JWT 토큰 검증을 수행하는 Spring Security 필터
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    /**
     * 모든 HTTP 요청에 대해 JWT 토큰 검증을 수행
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException      IO 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (canSkipFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        var token = getTokenWithAuthorizationHeader(request);
        if (token == null) {
            handleTokenException(response, "잘못된 토큰 정보입니다.");
            log.info("토큰의 형식이 잘못되었습니다.");
            return;
        }
        var memberId = getMemberIdWithToken(request, token);
        if (memberId == null) {
            handleTokenException(response, "토큰이 만료되었습니다.");
            log.info("토큰[{}]의 유효기간이 만료되었습니다.", token);
            return;
        }

        setMemberAuthToken(request, memberId);

        filterChain.doFilter(request, response);
    }

    /**
     * 멤버 인증 토큰을 SecurityContext에 설정
     *
     * @param request  HTTP 요청 객체
     * @param memberId 멤버 ID
     */
    private void setMemberAuthToken(HttpServletRequest request, Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var authorities = List.of(new SimpleGrantedAuthority(member.getMemberRole()
                                                                   .name()));
        var authToken = new UsernamePasswordAuthenticationToken(member.getId(), member.getEmail(), authorities);
        var authDetails = new WebAuthenticationDetailsSource().buildDetails(request);
        authToken.setDetails(authDetails);

        SecurityContextHolder.getContext()
                             .setAuthentication(authToken);
    }

    /**
     * JWT 토큰에서 멤버 ID를 추출
     *
     * @param request HTTP 요청 객체
     * @param token   JWT 토큰
     * @return 멤버 ID (토큰이 유효하지 않으면 null)
     */
    private Long getMemberIdWithToken(HttpServletRequest request, String token) {
        try {
            return jwtProvider.getMemberIdWithToken(token);
        } catch (JwtException jwtException) {
            request.setAttribute("exception", "토큰이 만료되었습니다.");
            return null;
        }
    }

    /**
     * Authorization 헤더에서 JWT 토큰을 추출
     *
     * @param request HTTP 요청 객체
     * @return JWT 토큰 (헤더가 유효하지 않으면 null)
     */
    private String getTokenWithAuthorizationHeader(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            request.setAttribute("exception", "잘못된 토큰 정보입니다.");
            return null;
        }
        var header = authorizationHeader.split(" ");
        if (header.length != 2) {
            request.setAttribute("exception", "잘못된 토큰 정보입니다.");
            return null;
        }
        return header[1];
    }

    /**
     * 인증을 건너뛸 수 있는 요청인지 확인
     *
     * @param request HTTP 요청 객체
     * @return 인증을 건너뛸 수 있으면 true
     */
    private boolean canSkipFilter(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        var skipUris = List.of("/swagger-ui", "/swagger-resources", "/v3/api-docs", "/api/members/login", "/api/members/register", "/api/members/exists-email");
        var uri = request.getRequestURI();
        for (var skipUri : skipUris) {
            if (uri.startsWith(skipUri)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 토큰 관련 예외 발생 시 401 응답을 즉시 반환
     *
     * @param response HTTP 응답 객체
     * @param message  에러 메시지
     * @throws IOException IO 예외
     */
    private void handleTokenException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        var exceptionResponse = new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(), message);
        objectMapper.writeValue(response.getWriter(), exceptionResponse);
    }
}
