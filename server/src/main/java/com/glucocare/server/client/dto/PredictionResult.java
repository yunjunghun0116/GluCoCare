package com.glucocare.server.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PredictionResult(
        ResultDto result
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ResultDto(
            Map<String, PredictionDto> predictions
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PredictionDto(
            Double mean,
            Double[] pi90
    ) {
    }
}
