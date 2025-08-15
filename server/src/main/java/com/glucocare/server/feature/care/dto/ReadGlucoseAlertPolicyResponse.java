package com.glucocare.server.feature.care.dto;

public record ReadGlucoseAlertPolicyResponse(
        Long id,
        Long careGiverId,
        Integer highRiskValue,
        Integer veryHighRiskValue
) {
    public static ReadGlucoseAlertPolicyResponse of(Long id, Long careGiverId, Integer highRiskValue, Integer veryHighRiskValue) {
        return new ReadGlucoseAlertPolicyResponse(id, careGiverId, highRiskValue, veryHighRiskValue);
    }
}
