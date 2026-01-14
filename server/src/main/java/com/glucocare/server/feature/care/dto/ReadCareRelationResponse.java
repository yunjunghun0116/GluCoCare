package com.glucocare.server.feature.care.dto;

public record ReadCareRelationResponse(
        Long id,
        Long patientId,
        String patientName
) {
    public static ReadCareRelationResponse of(Long id, Long patientId, String patientName) {
        return new ReadCareRelationResponse(id, patientId, patientName);
    }
}
