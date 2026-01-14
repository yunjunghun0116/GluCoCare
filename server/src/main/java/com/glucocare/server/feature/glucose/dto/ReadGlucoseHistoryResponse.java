package com.glucocare.server.feature.glucose.dto;

import java.time.LocalDateTime;

public record ReadGlucoseHistoryResponse(
        Long id,
        LocalDateTime date,
        Integer sgv
) {
    public static ReadGlucoseHistoryResponse of(Long id, LocalDateTime date, Integer sgv) {
        return new ReadGlucoseHistoryResponse(id, date, sgv);
    }
}
