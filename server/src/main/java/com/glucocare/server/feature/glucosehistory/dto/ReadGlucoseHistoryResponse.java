package com.glucocare.server.feature.caregiver.dto;

public record ReadCareGiverResponse(
        Long id,
        Long patientId,
        String name,
        String cgmServerUrl
) {
    public static ReadCareGiverResponse of(Long id, Long patientId, String name, String cgmServerUrl) {
        return new ReadCareGiverResponse(id, patientId, name, cgmServerUrl);
    }
}
