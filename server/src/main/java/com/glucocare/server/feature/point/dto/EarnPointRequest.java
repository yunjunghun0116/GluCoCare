package com.glucocare.server.feature.point.dto;

import jakarta.validation.constraints.NotNull;

public record EarnPointRequest(
        @NotNull(message = "포인트양은 반드시 입력되어야 합니다.") Long amount,
        @NotNull(message = "포인트 타입 설명은 반드시 입력되어야 합니다.") String description
) {
}
