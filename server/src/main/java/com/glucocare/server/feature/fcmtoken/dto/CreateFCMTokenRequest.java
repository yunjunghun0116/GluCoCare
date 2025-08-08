package com.glucocare.server.feature.fcmtoken.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFCMTokenRequest(
        @NotBlank(message = "FCM TOKEN 의 길이는 최소 1자 이상이어야 합니다.") String fcmToken
) {
}
