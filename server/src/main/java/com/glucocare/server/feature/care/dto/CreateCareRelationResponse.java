package com.glucocare.server.feature.care.dto;

public record CreateCareRelationResponse(
        Long id,
        Long patientId,
        String patientName
) {
    public static CreateCareRelationResponse of(Long id, Long patientId, String patientName) {
        return new CreateCareRelationResponse(id, patientId, patientName);
    }
}
