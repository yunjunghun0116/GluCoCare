package com.glucocare.server.feature.caregiver.dto;

public record CreateCareGiverResponse(
        Long id,
        Long memberId,
        Long patientId,
        String name
) {
    public static CreateCareGiverResponse of(Long id, Long memberId, Long patientId, String name) {
        return new CreateCareGiverResponse(id, memberId, patientId, name);
    }
}
