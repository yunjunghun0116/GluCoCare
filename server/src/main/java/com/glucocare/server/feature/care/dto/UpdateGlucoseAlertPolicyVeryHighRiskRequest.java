package com.glucocare.server.feature.care.dto;

import org.hibernate.validator.constraints.Range;

public record UpdateGlucoseAlertPolicyVeryHighRiskRequest(
        @Range(
                min = 180,
                message = "경고 2단계는 180이상이어야 합니다."
        ) Integer veryHighRiskValue
) {
}
