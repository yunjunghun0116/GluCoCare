package com.glucocare.server.security;

import com.glucocare.server.feature.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 서비스
 * JWT 토큰의 생성, 파싱, 검증 기능을 제공
 */
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    /**
     * 멤버 정보를 바탕으로 JWT 토큰을 생성
     * 
     * @param member 대상 멤버
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(Member member) {
        return Jwts.builder().subject(member.getId().toString()).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + jwtProperties.expiredTime())).signWith(Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes())).compact();

    }

    /**
     * JWT 토큰에서 멤버 ID를 추출
     * 
     * @param jwt JWT 토큰 문자열
     * @return 멤버 ID
     * @throws io.jsonwebtoken.JwtException 토큰이 유효하지 않은 경우
     */
    public Long getMemberIdWithToken(String jwt) {
        return Long.parseLong(decryptToken(jwt).getSubject());
    }

    /**
     * JWT 토큰을 디코딩하여 Claims 추출
     * 
     * @param jwt JWT 토큰 문자열
     * @return JWT Claims
     * @throws io.jsonwebtoken.JwtException 토큰이 유효하지 않은 경우
     */
    private Claims decryptToken(String jwt) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes())).build().parseSignedClaims(jwt).getPayload();
    }
}
