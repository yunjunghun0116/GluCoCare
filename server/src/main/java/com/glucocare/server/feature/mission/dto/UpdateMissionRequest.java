package com.glucocare.server.feature.mission.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateMissionRequest(
        @NotNull(message = "미션명은 반드시 입력되어야 합니다.") String title,
        @NotNull(message = "미션 소개는 반드시 입력되어야 합니다.") String description,
        @NotNull(message = "기준값은 반드시 입력되어야 합니다.(없을경우 0.0 입력)") Double threshold,
        @NotNull(message = "보상 포인트는 반드시 입력되어야 합니다.") Integer rewardPoint,
        @NotNull(message = "실행 여부는 반드시 입력되어야 합니다.") Boolean isActive
) {
}
