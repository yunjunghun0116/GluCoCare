package com.glucocare.server.feature.care.dto;

public record ReadGlucoseAlertPolicyResponse(
        Long id,
        Long careRelationId,
        Integer highRiskValue,
        Integer veryHighRiskValue,
        Integer lowRiskValue
) {
    public static ReadGlucoseAlertPolicyResponse of(Long id, Long careRelationId, Integer highRiskValue, Integer veryHighRiskValue, Integer lowRiskValue) {
        return new ReadGlucoseAlertPolicyResponse(id, careRelationId, highRiskValue, veryHighRiskValue, lowRiskValue);
    }
}
