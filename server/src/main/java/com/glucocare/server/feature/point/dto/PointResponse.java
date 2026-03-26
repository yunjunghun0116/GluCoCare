package com.glucocare.server.feature.point.dto;

public record PointResponse(
        Long point
) {
    public static PointResponse of(Long point) {
        return new PointResponse(point);
    }
}
