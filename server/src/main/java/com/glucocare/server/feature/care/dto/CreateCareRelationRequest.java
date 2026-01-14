package com.glucocare.server.feature.care.dto;

import com.glucocare.server.feature.care.domain.RelationType;
import jakarta.validation.constraints.NotNull;

public record CreateCareRelationRequest(
        @NotNull(message = "환자의 ID는 반드시 입력되어야 합니다.") Long patientId,
        @NotNull(message = "환자와의 관계는 반드시 입력되어야 합니다.") RelationType relationType
) {
}
