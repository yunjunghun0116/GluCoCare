package com.glucocare.server.feature.glucosehistory.dto;

public record ReadGlucoseHistoryResponse(
        Long id,
        Long date,
        Integer sgv
) {
    public static ReadGlucoseHistoryResponse of(Long id, Long date, Integer sgv) {
        return new ReadGlucoseHistoryResponse(id, date, sgv);
    }
}
