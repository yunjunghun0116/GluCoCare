package com.glucocare.server.feature.glucose.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateGlucoseHistoryRequest(
        @NotNull(message = "혈당 측정 일시는 반드시 입력되어야 합니다.") LocalDateTime dateTime,
        @NotNull(message = "혈당값은 반드시 입력되어야 합니다.") Integer value
) {
}
