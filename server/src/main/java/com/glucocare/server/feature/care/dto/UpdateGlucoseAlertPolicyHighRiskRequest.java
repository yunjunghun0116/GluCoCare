package com.glucocare.server.feature.care.dto;

import org.hibernate.validator.constraints.Range;

public record UpdateGlucoseAlertPolicyRequest(
        @Range(
                min = 100,
                max = 180,
                message = "경고 1단계는 100이상, 180이하여야 합니다."
        ) Integer highRiskValue,
        @Range(
                min = 181,
                message = "경고 2단계는 181이상이어야 합니다."
        ) Integer veryHighRiskValue
) {
}
