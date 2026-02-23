package com.glucocare.server.feature.glucose.dto;

import java.time.LocalDateTime;

public record PredictGlucoseResponse(
        Long id,
        LocalDateTime date,
        Double mean,
        Double min,
        Double max
) {
    public static PredictGlucoseResponse of(Long id, LocalDateTime date, Double mean, Double min, Double max) {
        return new PredictGlucoseResponse(id, date, mean, min, max);
    }
}
