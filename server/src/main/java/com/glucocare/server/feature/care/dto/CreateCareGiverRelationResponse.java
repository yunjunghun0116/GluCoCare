package com.glucocare.server.feature.care.dto;

public record CreateCareGiverRelationResponse(
        Long id,
        Long patientId,
        String patientName
) {
    public static CreateCareGiverRelationResponse of(Long id, Long patientId, String patientName) {
        return new CreateCareGiverRelationResponse(id, patientId, patientName);
    }
}
