package com.glucocare.server.feature.care.dto;

public record ReadCareGiverRelationResponse(
        Long id,
        Long patientId,
        String patientName
) {
    public static ReadCareGiverRelationResponse of(Long id, Long patientId, String patientName) {
        return new ReadCareGiverRelationResponse(id, patientId, patientName);
    }
}
