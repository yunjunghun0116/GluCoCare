package com.glucocare.server.security;

import com.glucocare.server.config.properties.JwtProperties;
import com.glucocare.server.feature.auth.dto.AuthResponse;
import com.glucocare.server.feature.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 토큰 제공자
 * <p>
 * JWT 토큰의 생성, 파싱, 검증 기능을 제공합니다.
 * Access Token과 Refresh Token을 생성하고, 토큰에서 회원 ID를 추출합니다.
 */
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    /**
     * Access Token과 Refresh Token 생성
     * <p>
     * 처리 단계:
     * 1. 회원 정보로 Access Token 생성
     * 2. 회원 정보로 Refresh Token 생성
     * 3. 두 토큰을 포함한 인증 응답 생성 및 반환
     *
     * @param member 토큰을 생성할 회원 엔티티
     * @return Access Token과 Refresh Token을 포함한 인증 응답
     */
    public AuthResponse generateToken(Member member) {
        var accessToken = generateAccessToken(member);
        var refreshToken = generateRefreshToken(member);
        return AuthResponse.of(accessToken, refreshToken);

    }

    /**
     * Access Token 생성
     * <p>
     * 처리 단계:
     * 1. JWT Builder로 토큰 생성 시작
     * 2. Subject에 회원 ID 설정
     * 3. 발행 시각을 현재 시각으로 설정
     * 4. 만료 시각을 현재 시각 + Access Token 유효 시간으로 설정
     * 5. Secret Key로 서명
     * 6. 토큰 문자열 생성 및 반환
     *
     * @param member 대상 회원 엔티티
     * @return 생성된 Access Token 문자열
     */
    private String generateAccessToken(Member member) {
        return Jwts.builder()
                   .subject(member.getId()
                                  .toString())
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiredTime()))
                   .signWith(Keys.hmacShaKeyFor(jwtProperties.secretKey()
                                                             .getBytes()))
                   .compact();

    }

    /**
     * Refresh Token 생성
     * <p>
     * 처리 단계:
     * 1. JWT Builder로 토큰 생성 시작
     * 2. Subject에 회원 ID 설정
     * 3. 발행 시각을 현재 시각으로 설정
     * 4. 만료 시각을 현재 시각 + Refresh Token 유효 시간으로 설정
     * 5. Secret Key로 서명
     * 6. 토큰 문자열 생성 및 반환
     *
     * @param member 대상 회원 엔티티
     * @return 생성된 Refresh Token 문자열
     */
    private String generateRefreshToken(Member member) {
        return Jwts.builder()
                   .subject(member.getId()
                                  .toString())
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiredTime()))
                   .signWith(Keys.hmacShaKeyFor(jwtProperties.secretKey()
                                                             .getBytes()))
                   .compact();

    }

    /**
     * JWT 토큰에서 회원 ID 추출
     * <p>
     * 처리 단계:
     * 1. JWT 토큰 복호화 및 Claims 추출
     * 2. Claims의 Subject에서 회원 ID 추출
     * 3. Long 타입으로 변환하여 반환
     *
     * @param jwt JWT 토큰 문자열
     * @return 회원 ID
     * @throws io.jsonwebtoken.JwtException 토큰이 유효하지 않은 경우
     */
    public Long getMemberIdWithToken(String jwt) {
        return Long.parseLong(decryptToken(jwt).getSubject());
    }

    /**
     * JWT 토큰 복호화 및 Claims 추출
     * <p>
     * 처리 단계:
     * 1. JWT Parser 생성
     * 2. Secret Key로 서명 검증
     * 3. 토큰 파싱 및 Claims 추출
     * 4. Claims 반환
     *
     * @param jwt JWT 토큰 문자열
     * @return JWT Claims
     * @throws io.jsonwebtoken.JwtException 토큰이 유효하지 않은 경우
     */
    private Claims decryptToken(String jwt) {
        return Jwts.parser()
                   .verifyWith(Keys.hmacShaKeyFor(jwtProperties.secretKey()
                                                               .getBytes()))
                   .build()
                   .parseSignedClaims(jwt)
                   .getPayload();
    }
}
