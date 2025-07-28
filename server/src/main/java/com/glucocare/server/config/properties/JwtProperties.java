package com.glucocare.server.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 설정 속성
 * application.yml의 jwt 설정을 매핑하는 설정 클래스
 *
 * @param secretKey   JWT 서명에 사용될 비밀키
 * @param expiredTime JWT 토큰 만료 시간 (밀리초)
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secretKey,
        Long expiredTime
) {
}
