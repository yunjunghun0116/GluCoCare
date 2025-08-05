package com.glucocare.server.feature.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "유효하지 않은 토큰정보입니다.") String token
) {
}
