package com.glucocare.server.feature.patient.dto;

public record ReadPatientResponse(
        Long id,
        String name,
        String cgmServerUrl
) {
    public static ReadPatientResponse of(Long id, String name, String cgmServerUrl) {
        return new ReadPatientResponse(id, name, cgmServerUrl);
    }
}
