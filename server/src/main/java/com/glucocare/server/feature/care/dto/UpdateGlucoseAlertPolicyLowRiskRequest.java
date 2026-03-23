package com.glucocare.server.feature.care.dto;

import org.hibernate.validator.constraints.Range;

public record UpdateGlucoseAlertPolicyLowRiskRequest(
        @Range(
                min = 40,
                max = 70,
                message = "저혈당 경고는 40이상, 70이하여야 합니다."
        ) Integer lowRiskValue
) {
}
