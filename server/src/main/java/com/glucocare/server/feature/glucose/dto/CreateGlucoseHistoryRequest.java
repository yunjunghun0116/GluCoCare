package com.glucocare.server.feature.glucose.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateGlucoseHistoryRequest(
        @NotNull(message = "환자의 ID 는 반드시 입력되어야 합니다.") Long careRelationId,
        @NotNull(message = "혈당 측정 일시는 반드시 입력되어야 합니다.") LocalDateTime dateTime,
        @Min(
                value = 20,
                message = "혈당 값은 20 이상이어야 합니다"
        ) @Max(
                value = 600,
                message = "혈당 값은 600 이하여야 합니다"
        ) @NotNull(message = "혈당값은 반드시 입력되어야 합니다.") Integer sgv
) {
}
