package com.glucocare.server.feature.patient.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCgmServerUrlRequest(
        @NotBlank(message = "CGM 서버는 반드시 입력되어야 합니다.") String cgmServerUrl
) {
}
