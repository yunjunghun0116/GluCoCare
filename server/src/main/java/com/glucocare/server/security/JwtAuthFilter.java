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
 * <p>
 * 모든 HTTP 요청에 대해 JWT 토큰 검증을 수행하는 Spring Security 필터입니다.
 * Authorization 헤더에서 JWT 토큰을 추출하여 검증하고, 유효한 경우 SecurityContext에 인증 정보를 설정합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    /**
     * 모든 HTTP 요청에 대해 JWT 토큰 검증 수행
     * <p>
     * 처리 단계:
     * 1. 인증을 건너뛸 수 있는 요청인지 확인 (OPTIONS, 토큰 존재하지 않는 요청 등)
     * 2. 건너뛸 수 있으면 다음 필터로 이동
     * 3. Authorization 헤더에서 JWT 토큰 추출
     * 4. 토큰이 없거나 형식이 잘못되었으면 401 응답 반환
     * 5. 토큰에서 회원 ID 추출
     * 6. 토큰이 만료되었으면 401 응답 반환
     * 7. SecurityContext에 인증 정보 설정
     * => 헤더가 필요한 요청의 경우는 SecurityContext 를 통해 isAuthenticated() 가 true 일때만 호출할 수 있기 때문에 이곳에서 인증 처리를 해주는게 필요
     * => OPTIONS, 회원가입, 로그인 등 토큰이 존재하지 않는 경우에는 SecurityConfig 에서 permitAll() 을 통해 다음 단계로 넘어갈 수 있도록 해주어야 함.
     * 8. 다음 필터로 이동
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
     * SecurityContext에 인증 정보 설정
     * <p>
     * 처리 단계:
     * 1. 회원 ID로 데이터베이스에서 회원 엔티티 조회
     * 2. 회원의 역할(Role)로 권한 목록 생성
     * 3. 회원 ID, 이메일, 권한 목록으로 인증 토큰 생성
     * 4. 요청 정보로 인증 세부 정보 생성 및 설정
     * 5. SecurityContext에 인증 토큰 설정
     *
     * @param request  HTTP 요청 객체
     * @param memberId 회원 ID
     * @throws ApplicationException 회원을 찾을 수 없는 경우 (NOT_FOUND)
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
     * JWT 토큰에서 회원 ID 추출
     * <p>
     * 처리 단계:
     * 1. JwtProvider로 토큰에서 회원 ID 추출 시도
     * 2. 성공하면 회원 ID 반환
     * 3. 실패하면 요청 속성에 에러 메시지 설정 후 null 반환
     *
     * @param request HTTP 요청 객체
     * @param token   JWT 토큰
     * @return 회원 ID (토큰이 유효하지 않으면 null)
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
     * Authorization 헤더에서 JWT 토큰 추출
     * <p>
     * 처리 단계:
     * 1. Authorization 헤더 조회
     * 2. 헤더를 공백으로 분리
     * 3. 분리된 배열의 길이가 2가 아니면 에러 메시지 설정 후 null 반환
     * 4. 두 번째 요소(토큰 문자열) 반환
     *
     * @param request HTTP 요청 객체
     * @return JWT 토큰 (헤더가 유효하지 않으면 null)
     */
    private String getTokenWithAuthorizationHeader(HttpServletRequest request) {
        var auth = request.getHeader("Authorization");
        var header = auth.split(" ");
        if (header.length != 2) {
            request.setAttribute("exception", "잘못된 토큰 정보입니다.");
            return null;
        }
        return header[1];
    }

    /**
     * 인증을 건너뛸 수 있는 요청인지 확인
     * <p>
     * 처리 단계:
     * 1. OPTIONS 메서드면 true 반환 (CORS Preflight 요청)
     * 2. 인증 헤더가 존재할 경우에 Bearer 인증 방식이 아닐 경우에 true 반환
     * 3. 인증 헤더가 존재하지 않을 경우에 true 반환
     * 4. 인증 헤더가 존재하고, Bearer 인증 방식일 경우 false 반환
     *
     * @param request HTTP 요청 객체
     * @return 인증을 건너뛸 수 있으면 true, 그렇지 않으면 false
     */
    private boolean canSkipFilter(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        var skipUris = List.of("/swagger-ui", "/swagger-resources", "/v3/api-docs", "/api/members/login", "/api/members/register", "/api/members/exists-email", "/api/members/refresh-token", "/api/oauth");
        var uri = request.getRequestURI();
        for (var skipUri : skipUris) {
            if (uri.startsWith(skipUri)) {
                return true;
            }
        }

        var auth = request.getHeader("Authorization");
        return auth == null || !auth.startsWith("Bearer ");
    }

    /**
     * 토큰 예외 발생 시 401 Unauthorized 응답 즉시 반환
     * <p>
     * 처리 단계:
     * 1. 응답 상태코드를 401로 설정
     * 2. Content-Type을 application/json으로 설정
     * 3. Character-Encoding을 UTF-8로 설정
     * 4. 예외 응답 객체 생성 (상태코드 401, 에러 메시지 포함)
     * 5. JSON 형태로 예외 응답 작성
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
