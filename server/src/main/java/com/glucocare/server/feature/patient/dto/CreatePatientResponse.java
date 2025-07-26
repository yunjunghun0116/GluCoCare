package com.glucocare.server.feature.patient.dto;

public record CreatePatientResponse(
        Long id,
        String name,
        String cgmServerUrl
) {
    public static CreatePatientResponse of(Long id, String name, String cgmServerUrl) {
        return new CreatePatientResponse(id, name, cgmServerUrl);
    }
}
