package com.glucocare.server.feature.glucose.dto;

public record PredictGlucoseResponse(
        Long id,
        Long dateTime,
        Double mean,
        Double min,
        Double max
) {
    public static PredictGlucoseResponse of(Long id, Long dateTime, Double mean, Double min, Double max) {
        return new PredictGlucoseResponse(id, dateTime, mean, min, max);
    }
}
