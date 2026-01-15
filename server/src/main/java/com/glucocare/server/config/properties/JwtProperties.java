package com.glucocare.server.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secretKey,
        Long accessTokenExpiredTime,
        Long refreshTokenExpiredTime
) {
}
