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

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    public AuthResponse generateToken(Member member) {
        var accessToken = generateAccessToken(member);
        var refreshToken = generateRefreshToken(member);
        return AuthResponse.of(accessToken, refreshToken);

    }

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

    public Long getMemberIdWithToken(String jwt) {
        return Long.parseLong(decryptToken(jwt).getSubject());
    }

    private Claims decryptToken(String jwt) {
        return Jwts.parser()
                   .verifyWith(Keys.hmacShaKeyFor(jwtProperties.secretKey()
                                                               .getBytes()))
                   .build()
                   .parseSignedClaims(jwt)
                   .getPayload();
    }
}
