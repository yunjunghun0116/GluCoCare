package com.glucocare.server.feature.notification.dto;

public record CreateFCMTokenResponse(
        Long id,
        String fcmToken
) {
    public static CreateFCMTokenResponse of(Long id, String fcmToken) {
        return new CreateFCMTokenResponse(id, fcmToken);
    }
}
