package com.glucocare.server.feature.glucose.dto;

import java.time.LocalDate;

public record ReadGlucoseSyncDateResponse(
        Long patientId,
        String patientName,
        LocalDate date
) {
    public static ReadGlucoseSyncDateResponse of(Long patientId, String patientName, LocalDate date) {
        return new ReadGlucoseSyncDateResponse(patientId, patientName, date);
    }
}
