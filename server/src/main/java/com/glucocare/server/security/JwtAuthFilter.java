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

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

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

    private Long getMemberIdWithToken(HttpServletRequest request, String token) {
        try {
            return jwtProvider.getMemberIdWithToken(token);
        } catch (JwtException jwtException) {
            request.setAttribute("exception", "토큰이 만료되었습니다.");
            return null;
        }
    }

    private String getTokenWithAuthorizationHeader(HttpServletRequest request) {
        var auth = request.getHeader("Authorization");
        var authHeader = auth.split(" ");
        if (authHeader.length != 2) {
            request.setAttribute("exception", "잘못된 토큰 정보입니다.");
            return null;
        }
        return authHeader[1];
    }

    private boolean canSkipFilter(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        var skipUris = List.of("/swagger", "/swagger-ui", "/v3/api-docs", "/swagger-resources", "/api/members/login", "/api/members/register", "/api/members/refresh-token", "/api/members/exists-email", "/api/members/confirm", "/api/oauth", "/api/v1");
        var uri = request.getRequestURI();
        if (uri.contains("/api/v1")) return true;
        for (var skipUri : skipUris) {
            if (uri.startsWith(skipUri)) return true;
        }

        var auth = request.getHeader("Authorization");
        return auth == null || !auth.startsWith("Bearer ");
    }

    private void handleTokenException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        var exceptionResponse = new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(), message);
        objectMapper.writeValue(response.getWriter(), exceptionResponse);
    }
}
