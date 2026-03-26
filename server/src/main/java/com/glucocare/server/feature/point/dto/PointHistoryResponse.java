package com.glucocare.server.feature.point.dto;

import com.glucocare.server.feature.point.domain.PointTransactionType;

public record PointHistoryResponse(
        PointTransactionType type,
        Long amount,
        Long balanceAfter,
        String description
) {
    public static PointHistoryResponse of(PointTransactionType type, Long amount, Long balanceAfter, String description) {
        return new PointHistoryResponse(type, amount, balanceAfter, description);
    }
}
