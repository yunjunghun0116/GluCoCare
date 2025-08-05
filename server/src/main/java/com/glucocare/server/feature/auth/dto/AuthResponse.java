package com.glucocare.server.feature.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
    public static AuthResponse of(String accessToken, String refreshToken) {
        return new AuthResponse(accessToken, refreshToken);
    }
}
