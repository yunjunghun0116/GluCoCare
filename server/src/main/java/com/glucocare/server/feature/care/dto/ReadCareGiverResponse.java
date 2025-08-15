package com.glucocare.server.feature.care.dto;

public record ReadCareGiverResponse(
        Long id,
        Long patientId,
        String patientName
) {
    public static ReadCareGiverResponse of(Long id, Long patientId, String patientName) {
        return new ReadCareGiverResponse(id, patientId, patientName);
    }
}
