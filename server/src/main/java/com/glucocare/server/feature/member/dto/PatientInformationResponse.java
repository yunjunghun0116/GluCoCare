package com.glucocare.server.feature.member.dto;

public record PatientInformationResponse(
        Long id,
        String name,
        Boolean isPatient,
        String accessCode
) {
    public static PatientInformationResponse of(Long id, String name, Boolean isPatient, String accessCode) {
        return new PatientInformationResponse(id, name, isPatient, accessCode);
    }
}
