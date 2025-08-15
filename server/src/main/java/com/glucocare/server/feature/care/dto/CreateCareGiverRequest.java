package com.glucocare.server.feature.care.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCareGiverRequest(
        @NotBlank(message = "이름의 길이는 최소 1자 이상이어야 합니다.") String name,
        @NotNull(message = "환자의 ID는 반드시 입력되어야 합니다.") Long patientId
) {
}
