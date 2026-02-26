package com.glucocare.server.feature.glucose.dto;

public record ReadGlucoseHistoryResponse(
        Long id,
        Long dateTime,
        Integer sgv
) {
    public static ReadGlucoseHistoryResponse of(Long id, Long dateTime, Integer sgv) {
        return new ReadGlucoseHistoryResponse(id, dateTime, sgv);
    }
}
