package com.glucocare.server.feature.care.dto;

public record CreateCareGiverResponse(
        Long id,
        Long patientId,
        String patientName
) {
    public static CreateCareGiverResponse of(Long id, Long patientId, String patientName) {
        return new CreateCareGiverResponse(id, patientId, patientName);
    }
}
