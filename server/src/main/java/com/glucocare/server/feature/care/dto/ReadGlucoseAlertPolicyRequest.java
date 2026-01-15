package com.glucocare.server.feature.care.dto;

import jakarta.validation.constraints.NotNull;

public record ReadGlucoseAlertPolicyRequest(
        @NotNull(message = "CareRelation 의 ID는 반드시 입력되어야 합니다.") Long careRelationId
) {
}
