package com.glucocare.server.feature.caregiver.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCareGiverRequest(
        @NotBlank(message = "이름의 길이는 최소 1자 이상이어야 합니다.") String name,
        @NotBlank(message = "CGM 서버는 반드시 입력되어야 합니다.") String cgmServerUrl
) {
}
