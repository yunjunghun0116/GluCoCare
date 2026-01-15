package com.glucocare.server.feature.care.dto;

public record ReadGlucoseAlertPolicyResponse(
        Long id,
        Long careRelationId,
        Integer highRiskValue,
        Integer veryHighRiskValue
) {
    public static ReadGlucoseAlertPolicyResponse of(Long id, Long careRelationId, Integer highRiskValue, Integer veryHighRiskValue) {
        return new ReadGlucoseAlertPolicyResponse(id, careRelationId, highRiskValue, veryHighRiskValue);
    }
}
